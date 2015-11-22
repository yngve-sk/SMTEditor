package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import application_componentview.Components;

/**
 * Contains some test data, four trees, along with their correct costs
 * @author Yngve Sekse Kristiansen
 *
 */
public enum TestData {
	One(10500),
	Two(11750),
	Three(16100),
	Four(6350);
	
	public int getCorrectCost() {
		return this.correctCost;
	}
	
	public String getFilePath() {
		return "src/smt_data/" + Integer.toString(this.correctCost);
	}
	
	private int correctCost;
	
	TestData(int cost) {
		this.correctCost = cost;
	}
}
