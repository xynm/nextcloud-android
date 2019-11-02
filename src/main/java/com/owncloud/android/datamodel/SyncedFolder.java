/*
 *   Nextcloud Android client application
 *
 *   @author Tobias Kaminsky
 *   Copyright (C) 2016 Tobias Kaminsky
 *   Copyright (C) 2016 Nextcloud
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 *   License as published by the Free Software Foundation; either
 *   version 3 of the License, or any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.datamodel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Synced folder entity containing all information per synced folder.
 */
@Getter
@Setter
@AllArgsConstructor
public class SyncedFolder implements Serializable, Cloneable {
    public static final long UNPERSISTED_ID = Long.MIN_VALUE;
    private static final long serialVersionUID = -793476118299906429L;

    private long id = UNPERSISTED_ID;
    private String localPath;
    private String remotePath;
    private boolean wifiOnly;
    private boolean chargingOnly;
    private boolean subfolderByDate;
    private String account;
    private int uploadAction;
    private boolean enabled;
    private MediaFolderType type;
    private boolean hidden;

    /**
     * constructor for new, to be persisted entity.
     *
     * @param localPath       local path
     * @param remotePath      remote path
     * @param wifiOnly        upload on wifi only flag
     * @param chargingOnly    upload on charging only
     * @param subfolderByDate create sub-folders by date (month)
     * @param account         the account owning the synced folder
     * @param uploadAction    the action to be done after the upload
     * @param enabled         flag if synced folder config is active
     * @param type            the type of the folder
     * @param hidden          hide item flag
     */
    public SyncedFolder(String localPath, String remotePath, boolean wifiOnly, boolean chargingOnly,
                        boolean subfolderByDate, String account, int uploadAction, boolean enabled,
                        MediaFolderType type, boolean hidden) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.wifiOnly = wifiOnly;
        this.chargingOnly = chargingOnly;
        this.subfolderByDate = subfolderByDate;
        this.account = account;
        this.uploadAction = uploadAction;
        this.enabled = enabled;
        this.type = type;
        this.hidden = hidden;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
