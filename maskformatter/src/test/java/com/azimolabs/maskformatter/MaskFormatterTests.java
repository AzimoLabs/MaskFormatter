package com.azimolabs.maskformatter;

import android.text.InputType;
import android.widget.EditText;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by maciek on 16.03.2016.
 */
public class MaskFormatterTests {

    MaskFormatter filter;

    @Mock
    EditText mockEditText;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        String mask = "99 AAAA wwww wwww wwww wwww";
        filter = new MaskFormatter(mask, mockEditText);
    }

    @Test
    public void testShouldFormatProperlyAddedCharToTextWithIBANMask() {
        when(mockEditText.getSelectionEnd()).thenReturn(2);

        filter.beforeTextChanged("34", 2, 0, 1);
        filter.onTextChanged("34s", 2, 0, 1);

        verify(mockEditText).setText("34 S");

        filter.onTextChanged("34 S", 2, 0, 1);
        verify(mockEditText).setSelection(4);

        // ----

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(3);

        filter.beforeTextChanged("34 S", 2, 0, 1);
        filter.onTextChanged("34 aS", 2, 0, 1);

        verify(mockEditText).setText("34 AS");

        filter.onTextChanged("34 AS", 2, 0, 1);
        verify(mockEditText).setSelection(4);

        // ----

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(4);

        filter.beforeTextChanged("34 AS", 2, 0, 1);
        filter.onTextChanged("34 A1S", 2, 0, 1);

        verify(mockEditText).setText("34 AS");

        filter.onTextChanged("34 AS", 2, 0, 1);
        verify(mockEditText).setSelection(4);

        // ----

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(27);

        filter.beforeTextChanged("34 ASFG asdf qwer 3412 rter", 2, 0, 1);
        filter.onTextChanged("34 ASFG asdf qwer 3412 rtera", 2, 0, 1);

        verify(mockEditText).setText("34 ASFG asdf qwer 3412 rter");

        filter.onTextChanged("34 ASFG asdf qwer 3412 rter", 2, 0, 1);
        verify(mockEditText).setSelection(27);

        // ----

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(9);

        filter.beforeTextChanged("34 ASFG asdf qwer 3412 rter", 2, 0, 1);
        filter.onTextChanged("34 ASFG assdf qwer 3412 rter", 2, 0, 1);

        verify(mockEditText).setText("34 ASFG asdf qwer 3412 rter");

        filter.onTextChanged("34 ASFG asdf qwer 3412 rter", 2, 0, 1);
        verify(mockEditText).setSelection(9);

    }

    @Test
    public void testShouldFormatProperlyPastedFragmentToTextWithIBANMask() {
        when(mockEditText.getSelectionEnd()).thenReturn(0);

        filter.beforeTextChanged("", 0, 0, 1);
        filter.onTextChanged("34ASFGasdfqwer3412rter", 0, 0, 22);

        verify(mockEditText).setText("34 ASFG asdf qwer 3412 rter");

        filter.onTextChanged("34 ASFG assdf qwer 3412 rter", 0, 0, 28);

        // ----

        filter.beforeTextChanged("34 ASF", 4, 0, 1);
        filter.onTextChanged("34 ASqwerF", 4, 0, 4);

        verify(mockEditText).setText("34 ASQW erF");
    }

    @Test
    public void testShouldFormatProperlyRemovedCharToTextWithIBANMask() {
        when(mockEditText.getSelectionEnd()).thenReturn(4);

        filter.beforeTextChanged("34 S", 3, 1, 0);
        filter.onTextChanged("34 ", 3, 1, 0);

        verify(mockEditText).setText("34");

        filter.onTextChanged("34", 2, 1, 0);
        verify(mockEditText).setSelection(2);

        // ----

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(3);

        filter.beforeTextChanged("34 SGHA a", 3, 1, 0);
        filter.onTextChanged("34 GHA a ", 3, 1, 0);

        verify(mockEditText).setText("34 GHAA");

        filter.onTextChanged("34 GHAA ", 7, 1, 0);

        verify(mockEditText).setSelection(2);

        // ---

        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(2);

        filter.beforeTextChanged("35 S", 2, 1, 0);
        filter.onTextChanged("3 S", 2, 1, 0);

        verify(mockEditText).setText("35 S");

        filter.onTextChanged("35 S", 1, 0, 1);

        verify(mockEditText).setSelection(2);
    }

    @Test
    public void testShouldFormatProperlyCutFragmentToTextWithIBANMask() {
        when(mockEditText.getSelectionEnd()).thenReturn(7);

        filter.beforeTextChanged("34 SGAH refa r", 7, 3, 0);
        filter.onTextChanged("34 S refa r", 7, 3, 0);

        verify(mockEditText).setText("34 SREF ar");
    }

    @Test
    public void testShouldSetProperInputTypeToTextWithIBANMask() {
        reset(mockEditText);
        String fieldCurrentValue = "1";
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText).setInputType(InputType.TYPE_CLASS_NUMBER);

        // ---

        reset(mockEditText);
        fieldCurrentValue = "11";
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        // ---

        reset(mockEditText);
        fieldCurrentValue = "11 ABCD cd";
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText).setInputType(InputType.TYPE_CLASS_TEXT);

        // ---

        fieldCurrentValue = "99 ABCD acbd 1234 efgh ijkl";
        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText, never()).setInputType(anyInt());
    }

    @Test
    public void testShouldSetProperInputTypeToPassword() {
        when(mockEditText.getInputType()).thenReturn(InputType.TYPE_TEXT_VARIATION_PASSWORD
            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        String mask = "99 AAAA wwww wwww wwww wwww";
        filter = new MaskFormatter(mask, mockEditText);

        String fieldCurrentValue = "99";
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD
            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        // ---

        fieldCurrentValue = "99 ABCD";
        reset(mockEditText);
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    @Test
    public void testShouldSetProperInputTypeToNumberPassword() {
        when(mockEditText.getInputType()).thenReturn(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        String mask = "999 99 9999";
        filter = new MaskFormatter(mask, mockEditText);

        String fieldCurrentValue = "123";
        when(mockEditText.getSelectionEnd()).thenReturn(fieldCurrentValue.length());

        filter.afterTextChanged(null);

        verify(mockEditText, times(2)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }
}
