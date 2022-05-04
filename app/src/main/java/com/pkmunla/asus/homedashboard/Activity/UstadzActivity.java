/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.mtp;

import android.content.IContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import java.util.ArrayList;

class MtpPropertyGroup {

    private static final String TAG = "MtpPropertyGroup";

    private class Property {
        // MTP property code
        int     code;
        // MTP data type
        int     type;
        // column index for our query
        int     column;

        Property(int code, int type, int column) {
            this.code = code;
            this.type = type;
            this.column = column;
        }
    }

    private final MtpDatabase mDatabase;
    private final IContentProvider mProvider;
    private final String mPackageName;
    private final String mVolumeName;
    private final Uri mUri;

    // list of all properties in this group
    private final Property[]    mProperties;

    // list of columns for database query
    private String[]             mColumns;

    private static final String ID_WHERE = Files.FileColumns._ID + "=?";
    private static final String FORMAT_WHERE = Files.FileColumns.FORMAT + "=?";
    private static final String ID_FORMAT_WHERE = ID_WHERE + " AND " + FORMAT_WHERE;
    private static final String PARENT_WHERE = Files.FileColumns.PARENT + "=?";
    private static final String PARENT_FORMAT_WHERE = PARENT_WHERE + " AND " + FORMAT_WHERE;
    // constructs a property group for a list of properties
    public MtpPropertyGroup(MtpDatabase database, IContentProvider provider, String packageName,
            String volume, int[] properties) {
        mDatabase = database;
        mProvider = provider;
        mPackageName = packageName;
        mVolumeName = volume;
        mUri = Files.getMtpObjectsUri(volume);

        int count = properties.length;
        ArrayList<String> columns = new ArrayList<String>(count);
        columns.add(Files.FileColumns._ID);

        mProperties = new Property[count];
        for (int i = 0; i < count; i++) {
            mProperties[i] = createProperty(properties[i], columns);
        }
        count = columns.size();
        mColumns = new String[count];
        for (int i = 0; i < count; i++) {
            mColumns[i] = columns.get(i);
        }
    }

    private Property createProperty(int code, ArrayList<String> columns) {
        String column = null;
        int type;

         switch (code) {
            case MtpConstants.PROPERTY_STORAGE_ID:
                column = Files.FileColumns.STORAGE_ID;
                type = MtpConstants.TYPE_UINT32;
                break;
             case MtpConstants.PROPERTY_OBJECT_FORMAT:
                column = Files.FileColumns.FORMAT;
                type = MtpConstants.TYPE_UINT16;
                break;
            case MtpConstants.PROPERTY_PROTECTION_STATUS:
                // protection status is always 0
                type = MtpConstants.TYPE_UINT16;
                break;
            case MtpConstants.PROPERTY_OBJECT_SIZE:
                column = Files.FileColumns.SIZE;
                type = MtpConstants.TYPE_UINT64;
                break;
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME:
                column = Files.FileColumns.DATA;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_NAME:
                column = MediaColumns.TITLE;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_DATE_MODIFIED:
                column = Files.FileColumns.DATE_MODIFIED;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_DATE_ADDED:
                column = Files.FileColumns.DATE_ADDED;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE:
                column = Audio.AudioColumns.YEAR;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_PARENT_OBJECT:
                column = Files.FileColumns.PARENT;
                type = MtpConstants.TYPE_UINT32;
                break;
            case MtpConstants.PROPERTY_PERSISTENT_UID:
                // PUID is concatenation of storageID and object handle
                column = Files.FileColumns.STORAGE_ID;
                type = MtpConstants.TYPE_UINT128;
                break;
            case MtpConstants.PROPERTY_DURATION:
                column = Audio.AudioColumns.DURATION;
                type = MtpConstants.TYPE_UINT32;
                break;
            case MtpConstants.PROPERTY_TRACK:
                column = Audio.AudioColumns.TRACK;
                type = MtpConstants.TYPE_UINT16;
                break;
            case MtpConstants.PROPERTY_DISPLAY_NAME:
                column = MediaColumns.DISPLAY_NAME;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_ARTIST:
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_ALBUM_NAME:
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_ALBUM_ARTIST:
                column = Audio.AudioColumns.ALBUM_ARTIST;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_GENRE:
                // genre requires a special query
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_COMPOSER:
                column = Audio.AudioColumns.COMPOSER;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_DESCRIPTION:
                column = Images.ImageColumns.DESCRIPTION;
                type = MtpConstants.TYPE_STR;
                break;
            case MtpConstants.PROPERTY_AUDIO_WAVE_CODEC:
            case MtpConstants.PROPERTY_AUDIO_BITRATE:
            case MtpConstants.PROPERTY_SAMPLE_RATE:
                // these are special cased
                type = MtpConstants.TYPE_UINT32;
                break;
            case MtpConstants.PROPERTY_BITRATE_TYPE:
            case MtpConstants.PROPERTY_NUMBER_OF_CHANNELS:
                // these are special cased
                type = MtpConstants.TYPE_UINT16;
                break;
            default:
                type = MtpConstants.TYPE_UNDEFINED;
                Log.e(TAG, "unsupported property " + code);
                break;
        }

        if (column != null) {
            columns.add(column);
            return new Property(code, type, columns.size() - 1);
        } else {
            return new Property(code, type, -1);
        }
    }

   private String queryString(int id, String column) {
        Cursor c = null;
        try {
            // for now we are only reading properties from the "objects" table
            c = mProvider.query(mPackageName, mUri,
                            new String [] { Files.FileColumns._ID, column },
                            ID_WHERE, new String[] { Integer.toString(id) }, null, null);
            if (c != null && c.moveToNext()) {
                return c.getString(1);
            } else {
                return "";
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private String queryAudio(int id, String column) {
        Cursor c = null;
        try {
            c = mProvider.query(mPackageName, Audio.Media.getContentUri(mVolumeName),
                            new String [] { Files.FileColumns._ID, column },
                            ID_WHERE, new String[] { Integer.toString(id) }, null, null);
            if (c != null && c.moveToNext()) {
                return c.getString(1);
            } else {
                return "";
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private String queryGenre(int id) {
        Cursor c = null;
        try {
            Uri uri = Audio.Genres.getContentUriForAudioId(mVolumeName, id);
            c = mProvider.query(mPackageName, uri,
                            new String [] { Files.FileColumns._ID, Audio.GenresColumns.NAME },
                            null, null, null, null);
            if (c != null && c.moveToNext()) {
                return c.getString(1);
            } else {
                return "";
            }
        } catch (Exception e) {
            Log.e(TAG, "queryGenre exception", e);
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private Long queryLong(int id, String column) {
        Cursor c = null;
        try {
            // for now we are only reading properties from the "objects" table
            c = mProvider.query(mPackageName, mUri,
                            new String [] { Files.FileColumns._ID, column },
                            ID_WHERE, new String[] { Integer.toString(id) }, null, null);
            if (c != null && c.moveToNext()) {
                return new Long(c.getLong(1));
            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    private static String nameFromPath(String path) {
        /