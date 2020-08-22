package com.bigocoding.secretalbum;

import android.net.Uri;

public class Photo {
    private String _title ;
    private Uri _uri ;

    public Photo(String _title, Uri _uri) {
        this._title = _title;
        this._uri = _uri;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public Uri get_uri() {
        return _uri;
    }

    public void set_uri(Uri _uri) {
        this._uri = _uri;
    }
}
