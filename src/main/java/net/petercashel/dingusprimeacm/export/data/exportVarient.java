package net.petercashel.dingusprimeacm.export.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class exportVarient {
    @Expose
    public int ID;
    @Expose
    public boolean hasNBT = false;
    @Expose
    public String key = "";
    @Expose
    public String TagString = "";
    @Expose
    public String DisplayName = "";
    public ArrayList<String> TooltipLines = new ArrayList<>();

    public exportVarient(int varientID, String Key) {
        ID = varientID;
        key = Key;
    }
}
