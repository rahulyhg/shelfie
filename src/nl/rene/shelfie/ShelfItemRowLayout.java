package nl.rene.shelfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShelfItemRowLayout extends RelativeLayout {


    private final ImageButton decrementButton;
    private final ImageButton incrementButton;
    private final ImageButton upArrow;
    private final ImageButton downArrow;
    private final TextView nameView;
    private final TextView desiredAmountView;

    public TextView getDesiredAmountView() {
        return desiredAmountView;
    }

    public ImageButton getDecrementButton() {

        return decrementButton;
    }

    public ImageButton getIncrementButton() {
        return incrementButton;
    }

    public ImageButton getUpArrow() {
        return upArrow;
    }

    public ImageButton getDownArrow() {
        return downArrow;
    }

    public ShelfItemRowLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.shelf_row, this);

        nameView = (TextView) findViewById(R.id.itemName);
        desiredAmountView = (TextView) findViewById(R.id.itemDesiredAmt);
        decrementButton = (ImageButton) findViewById(R.id.minAmt);
        incrementButton = (ImageButton) findViewById(R.id.plusAmt);
        upArrow = (ImageButton) findViewById(R.id.upButton);
        downArrow = (ImageButton) findViewById(R.id.downButton);

    }

    public void setName(String name) {
        this.nameView.setText(name);
    }

    public void setDesiredAmount(String desiredAmount) {
        this.desiredAmountView.setText(desiredAmount);
    }
}
