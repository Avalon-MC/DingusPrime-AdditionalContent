package net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation;

import eu.rekawek.coffeegb.controller.ButtonListener;
import eu.rekawek.coffeegb.controller.Controller;
import net.minecraft.client.KeyMapping;

import static net.petercashel.dingusprimeacm.dingusprimeacm_client.*;

public class GameboyController implements Controller {

    public ButtonListener listener;


    public Boolean GB_A_State = false;
    public Boolean GB_B_State = false;
    public Boolean GB_UP_State = false;
    public Boolean GB_DOWN_State = false;
    public Boolean GB_LEFT_State = false;
    public Boolean GB_RIGHT_State = false;
    public Boolean GB_SEL_State = false;
    public Boolean GB_START_State = false;

    @Override
    public void setButtonListener(ButtonListener listener) {
        this.listener = listener;
    }

    public void UpdateButtonState(ButtonListener.Button button, boolean newState) {
        if (listener != null) {
            if (newState) {
                listener.onButtonPress(button);
            } else {
                listener.onButtonRelease(button);
            }
        }
    }

    public void reset() {

    }

    private static Boolean HandleStateChange(Boolean oldState, boolean newState, ButtonListener.Button button) {
        if (oldState == newState) return oldState;
        controller.UpdateButtonState(button, newState);
        return newState;
    }

    public void UpdateBinding(KeyMapping mapping, boolean pressed) {
        switch (mapping.getName()) {
            case "key.GB_A": {
                GB_A_State = HandleStateChange(GB_A_State,pressed, ButtonListener.Button.A);
                break;
            }
            case "key.GB_B": {
                GB_B_State = HandleStateChange(GB_B_State,pressed, ButtonListener.Button.B);
                break;
            }
            case "key.GB_SEL": {
                GB_SEL_State = HandleStateChange(GB_SEL_State,pressed, ButtonListener.Button.SELECT);
                break;
            }
            case "key.GB_START": {
                GB_START_State = HandleStateChange(GB_START_State,pressed, ButtonListener.Button.START);
                break;
            }
            case "key.GB_UP": {
                GB_UP_State = HandleStateChange(GB_UP_State,pressed, ButtonListener.Button.UP);
                break;
            }
            case "key.GB_DOWN": {
                GB_DOWN_State = HandleStateChange(GB_DOWN_State,pressed, ButtonListener.Button.DOWN);
                break;
            }
            case "key.GB_LEFT": {
                GB_LEFT_State = HandleStateChange(GB_LEFT_State,pressed, ButtonListener.Button.LEFT);
                break;
            }
            case "key.GB_RIGHT": {
                GB_RIGHT_State = HandleStateChange(GB_RIGHT_State,pressed, ButtonListener.Button.RIGHT);
                break;
            }

        }
    }
}
