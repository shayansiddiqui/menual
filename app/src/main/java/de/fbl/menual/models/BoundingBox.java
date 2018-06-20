package de.fbl.menual.models;

import android.graphics.Rect;

import com.google.gson.JsonObject;

public class BoundingBox {
    Rect rect;
    JsonObject boxElement;

    public BoundingBox(Rect rect, JsonObject boxElement) {
        this.rect = rect;
        this.boxElement = boxElement;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public JsonObject getBoxElement() {
        return boxElement;
    }

    public void setBoxElement(JsonObject boxElement) {
        this.boxElement = boxElement;
    }
}
