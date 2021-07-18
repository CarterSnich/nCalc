package com.darkware.simpcalc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean newEntry = true;
    String observableValue;
    String oldValue;
    String newValue;
    String lastOperation;
    ArrayList<BigDecimal> nums = new ArrayList<BigDecimal>();
    ArrayList<String> operations = new ArrayList<String>();

    HorizontalScrollView entry_scroll_container;
    HorizontalScrollView preview_scroll_container;
    TextView entry_textview;
    TextView preview_textview;
    Button[] numButtons;
    Button[] operationButtons;
    Button decimalButton;
    Button backspace;
    Button clearButton;
    Button negateButton;
    Button equalsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer clickSound = MediaPlayer.create(this, R.raw.click);

        entry_scroll_container = findViewById(R.id.entry_scroll_container);
        preview_scroll_container = findViewById(R.id.preview_scroll_container);
        entry_textview = findViewById(R.id.entry_textview);
        preview_textview = findViewById(R.id.preview_textview);
        numButtons = new Button[] {
            findViewById(R.id.button0),
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9),
            findViewById(R.id.button00)
        };
        operationButtons = new Button[] {
            findViewById(R.id.addButton),
            findViewById(R.id.subtractButton),
            findViewById(R.id.multiButton),
            findViewById(R.id.divideButton)
        };
        decimalButton = findViewById(R.id.decimalButton);
        backspace = findViewById(R.id.backspaceButton);
        clearButton = findViewById(R.id.clearButton);
        negateButton = findViewById(R.id.negateButton);
        equalsButton = findViewById(R.id.equalsButton);

        // set hozirontal scroll view to automatically scroll to the right on text change
        entry_textview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                entry_scroll_container.post(new Runnable() {
                    @Override
                    public void run() {
                        entry_scroll_container.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                });
            }
        });

        entry_textview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("nCalc_copy", ((TextView) v).getText());
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(data);
                Toast.makeText(
                    getApplicationContext(),
                    "Copied to clipboard",
                    5
                ).show();

                return false;
            }
        });

        // set hozirontal scroll view to automatically scroll to the right on text change
        preview_textview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                preview_textview.post(new Runnable() {
                    @Override
                    public void run() {
                        preview_scroll_container.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                });
            }
        });

        // set click listeners to each number buttons
        for (final Button numButton: numButtons) {
            numButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickSound.start();

                    observableValue = numButton.getText().toString();
                    oldValue = entry_textview.getText().toString();

                    // checks if the next entry is a new value
                    if (newEntry) {
                        newValue = observableValue.contains("0")
                            ? "0"
                            : observableValue;
                        newEntry = false;
                    }

                    // filtering if current entry only holds "0"
                    else if (oldValue.equals("0")) {
                        newValue = observableValue.contains("0") ? "0" : observableValue;
                    }

                    // filters if the button entry is "0" or "00"
                    else {
                        newValue = oldValue.length() > 0
                            ? oldValue + observableValue
                            : observableValue.contains("0")
                                ? "0"
                                : observableValue;
                    }

                    entry_textview.setText(newValue);
                }

            });
        }

        // set click listeners to operation buttons
        for (final Button opButton : operationButtons) {
            opButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickSound.start();

                    String op = ((Button) v).getText().toString();
                    String currentEntry = entry_textview.getText().toString();

                    if (currentEntry.length() == 0) return;
                    if (newEntry) {
                        if (operations.size() == 0) {
                            nums.add(new BigDecimal(currentEntry));
                            operations.add(op);
                        } else {
                            operations.set(operations.size() - 1, op);
                        }
                    } else {
                        nums.add(new BigDecimal(currentEntry));
                        operations.add(op);
                        calculate();
                    }
                    newEntry = true;
                    updatePreview();
                }
            });
        }

        decimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();

                oldValue = entry_textview.getText().toString();
                if (newEntry) {
                    newValue = "0.";
                    newEntry = false;
                } else {
                    newValue = oldValue.equals("0")
                        ? "0."
                        : oldValue.matches("^.*\\d\\.\\d*$")
                            ? oldValue
                            : oldValue + ".";
                }
                entry_textview.setText(newValue);
            }
        });

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();

                oldValue = entry_textview.getText().toString();
                if (oldValue.length() > 1)
                    if (oldValue.matches("-\\d"))
                        newValue = "0";
                    else
                        newValue = oldValue.substring(0, oldValue.length()-1);
                else if (oldValue.length() == 1)
                    newValue = "0";

                entry_textview.setText(newValue);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();

                entry_textview.setText("0");
                preview_textview.setText("");
                nums.clear();
                operations.clear();
            }
        });

        negateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();

                oldValue = entry_textview.getText().toString();
                if (oldValue.length() > 0) {
                    entry_textview.setText(
                        new BigDecimal(oldValue).multiply(new BigDecimal("-1")).toString()
                    );
                }
            }
        });

        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();

                if (entry_textview.getText().length() > 0) {
                    nums.add(new BigDecimal(entry_textview.getText().toString()));
                    preview_textview.setText("");
                    calculate();
                    nums.clear();
                    operations.clear();
                    newEntry = true;
                }
            }
        });

    }

    public void calculate () {
        ArrayList<BigDecimal> temp = (ArrayList<BigDecimal>) nums.clone();
        try {
            for (int i = 0; i < temp.size()-1; i++) {
                switch (operations.get(i)) {
                    case "+":
                        temp.set(0, temp.get(0).add(temp.get(i+1)));
                        break;
                    case "−":
                        temp.set(0, temp.get(0).subtract(temp.get(i+1)));
                        break;
                    case "×":
                        temp.set(0, temp.get(0).multiply(temp.get(i+1)));
                        break;
                    case "÷":
                        temp.set(0, temp.get(0).divide(temp.get(i+1)));
                        break;
                }
            }
            BigDecimal x = temp.get(0).stripTrailingZeros();
            entry_textview.setText(
                x.toPlainString().length() > 16 ? format(x, 14) : x.toPlainString()
            );
        } catch (ArithmeticException arthmeticEx) {
            entry_textview.setText("0");
            preview_textview.setText("Cannot be divided by 0");
            nums.clear();
            operations.clear();
            newEntry = true;
        }
    }

    public void updatePreview () {
        preview_textview.setText("");

        for (BigDecimal num : nums) {
            System.out.println(nums);
        }

        for (String op : operations) {
            System.out.println(op);
        }

        BigDecimal x;
        for (int i = 0; i < nums.size(); i++) {
            x = nums.get(i).stripTrailingZeros();
            preview_textview.setText(String.format(
                "%s %s %s",
                preview_textview.getText(),
                x.toPlainString().length() > 15 ? format(x, 14) : x.toPlainString(),
                operations.get(i)
            ));
        }
    }

    private static String format(BigDecimal x, int scale) {
        NumberFormat formatter = new DecimalFormat("0.0E0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(scale);
        return formatter.format(x);
    }

}