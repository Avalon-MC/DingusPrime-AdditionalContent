package net.petercashel.dingusprimeacm.export;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.gson.annotations.Expose;
import net.petercashel.dingusprimeacm.export.data.BlockExport;
import net.petercashel.dingusprimeacm.export.data.FluidExport;
import net.petercashel.dingusprimeacm.export.data.GenericExport;
import net.petercashel.dingusprimeacm.export.data.ItemExport;

import java.util.ArrayList;

public class ExportedData {
    //    @Expose
    //    public ArrayList<BlockExport> Blocks = new ArrayList<>();
    @Expose
    public ArrayList<ItemExport> Items = new ArrayList<>();
    @Expose
    public ArrayList<FluidExport> Fluids = new ArrayList<>();

    @Expose
    public ArrayList<GenericExport> Enchantments = new ArrayList<>();

    @Expose
    public ArrayList<GenericExport> BlockTags = new ArrayList<>();
    @Expose
    public ArrayList<GenericExport> ItemTags = new ArrayList<>();


}
