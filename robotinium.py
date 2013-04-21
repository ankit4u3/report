package com.calculator.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.TextView;
import com.calculator.Main;
import com.calculator.R;
import com.jayway.android.robotium.solo.Solo;

public class TestMain extends ActivityInstrumentationTestCase2<Main> {
	private Solo solo;
	
	public TestMain() {
		super(Main.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testDisplayBlackBox() {

		//Enter 10 in first edit-field
		solo.enterText(0, "10");

		//Enter 20 in first edit-field
		solo.enterText(1, "20");

		//Click on Multiply button
		solo.clickOnButton("Multiply");

		//Verify that resultant of 10 x 20 
		assertTrue(solo.searchText("200"));

		}
	
	public void testDisplayWhiteBox() {

		//Defining our own values to multiply
		float firstNumber = 10;
		float secondNumber = 20;
		float resutl = firstNumber * secondNumber ;
		
		//Access First value (edit-filed) and putting firstNumber value in it
		EditText FirsteditText = (EditText) solo.getView(R.id.EditText01);
		solo.enterText(FirsteditText, String.valueOf(firstNumber));
		
		//Access Second value (edit-filed) and putting SecondNumber value in it
		EditText SecondeditText = (EditText) solo.getView(R.id.EditText02);
		solo.enterText(SecondeditText, String.valueOf(secondNumber));
		
		//Click on Multiply button
		solo.clickOnButton("Multiply");
		
		assertTrue(solo.searchText(String.valueOf(resutl)));				
		TextView outputField = (TextView) solo.getView(R.id.TextView01);		
		//Assert to verify result with visible value
		assertEquals(String.valueOf(resutl), outputField.getText().toString());
	}
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}
