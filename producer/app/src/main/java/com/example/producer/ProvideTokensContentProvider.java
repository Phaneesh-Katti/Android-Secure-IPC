package com.example.producer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ProvideTokensContentProvider extends ContentProvider {
    private TokenDatabaseHelper dbHelper;

    // Define the authority and content URIs
    public static final String AUTHORITY = "com.example.producer.tokenprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tokens");

    // UriMatcher codes
    private static final int TOKENS = 1;
    private static final int TOKEN_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "tokens", TOKENS);
        uriMatcher.addURI(AUTHORITY, "tokens/#", TOKEN_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TokenDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case TOKENS:
                cursor = dbHelper.getAllTokens();
                break;
            case TOKEN_ID:
                selection = TokenDatabaseHelper.COLUMN_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = dbHelper.getReadableDatabase().query(
                        TokenDatabaseHelper.TABLE_TOKENS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TOKENS:
                return "vnd.android.cursor.dir/tokens";
            case TOKEN_ID:
                return "vnd.android.cursor.item/token";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Content provider is read-only for external apps
        throw new UnsupportedOperationException("Read-only content provider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Content provider is read-only for external apps
        throw new UnsupportedOperationException("Read-only content provider");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Content provider is read-only for external apps
        throw new UnsupportedOperationException("Read-only content provider");
    }
}