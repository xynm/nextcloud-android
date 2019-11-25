/**
 *  ownCloud Android client application
 *
 *  @author Bartek Przybylski
 *  @author David A. Velasco
 *  Copyright (C) 2012 Bartek Przybylski
 *  Copyright (C) 2016 ownCloud Inc.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2,
 *  as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <p/>
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.datamodel.UploadsStorageManager;
import com.owncloud.android.db.OCUpload;
import com.owncloud.android.files.services.FileDownloader;
import com.owncloud.android.files.services.FileUploader;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.ui.dialog.ConflictsResolveDialog;
import com.owncloud.android.ui.dialog.ConflictsResolveDialog.Decision;
import com.owncloud.android.ui.dialog.ConflictsResolveDialog.OnConflictDecisionMadeListener;

import javax.inject.Inject;


/**
 * Wrapper activity which will be launched if keep-in-sync file will be modified by external
 * application.
 */
public class ConflictsResolveActivity extends FileActivity implements OnConflictDecisionMadeListener {
    public static final String EXTRA_CONFLICT_UPLOAD = "CONFLICT_UPLOAD";

    private static final String TAG = ConflictsResolveActivity.class.getSimpleName();

    @Inject UploadsStorageManager uploadsStorageManager;

    private OCUpload conflictUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            this.conflictUpload = savedInstanceState.getParcelable(EXTRA_CONFLICT_UPLOAD);
        } else {
            this.conflictUpload = getIntent().getParcelableExtra(EXTRA_CONFLICT_UPLOAD);
        }
    }

    @Override
    public void conflictDecisionMade(Decision decision) {
        OCFile file = getFile();
        FileUploader.UploadRequester uploadRequester = new FileUploader.UploadRequester();

        switch (decision) {
            case CANCEL:
                return;
            case KEEP_LOCAL:
            case KEEP_BOTH:
                Integer localBehaviour;
                FileUploader.NameCollisionPolicy collisionPolicy;
                if (decision == Decision.KEEP_LOCAL) {
                    // Overwrite remote file
                    localBehaviour = null;
                    collisionPolicy = FileUploader.NameCollisionPolicy.OVERWRITE;
                } else {
                    // Upload local version and rename
                    localBehaviour = FileUploader.LOCAL_BEHAVIOUR_MOVE;
                    collisionPolicy = FileUploader.NameCollisionPolicy.RENAME;
                }

                uploadRequester.uploadUpdate(this, getAccount(), file, localBehaviour, collisionPolicy);

                if (this.conflictUpload != null) {
                    uploadsStorageManager.removeUpload(this.conflictUpload);
                }
                break;
            case KEEP_SERVER:
                // Overwrite local file
                Intent intent = new Intent(this, FileDownloader.class);
                intent.putExtra(FileDownloader.EXTRA_ACCOUNT, getAccount());
                intent.putExtra(FileDownloader.EXTRA_FILE, file);
                if (this.conflictUpload != null) {
                    intent.putExtra(FileDownloader.EXTRA_CONFLICT_UPLOAD, this.conflictUpload);
                }
                startService(intent);
                break;
            default:
                Log_OC.e(TAG, "Unhandled conflict decision " + decision);
                return;
        }

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getAccount() != null) {
            OCFile file = getFile();
            if (getFile() == null) {
                Log_OC.e(TAG, "No file received");
                finish();
            } else {
                // Check whether the file is contained in the current Account
                if (getStorageManager().fileExists(file.getRemotePath())) {
                    ConflictsResolveDialog dialog = ConflictsResolveDialog.newInstance(this);
                    dialog.showDialog(this);
                } else {
                    // Account was changed to a different one - just finish
                    finish();
                }
            }
        } else {
            finish();
        }
    }
}
