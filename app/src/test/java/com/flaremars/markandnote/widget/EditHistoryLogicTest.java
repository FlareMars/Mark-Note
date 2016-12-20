package com.flaremars.markandnote.widget;

import android.widget.EditText;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by FlareMars on 2016/11/13.
 */
public class EditHistoryLogicTest {

    @Test
    public void testEditHistoryLogic() {
        EditText editText = Mockito.mock(EditText.class);
//        Mockito.when(editText.getText()).thenReturn();
        EditHistoryLogic logic = new EditHistoryLogic(editText);

        logic.redo();
    }
}