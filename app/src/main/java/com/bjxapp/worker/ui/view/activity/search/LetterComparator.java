package com.bjxapp.worker.ui.view.activity.search;

import java.util.Comparator;

public class LetterComparator implements Comparator<SearchModel> {

	public int compare(SearchModel first, SearchModel second) {
		if (first.getSortLetters().equals("@") || second.getSortLetters().equals("#")) {
			return -1;
		} else if (first.getSortLetters().equals("#") || second.getSortLetters().equals("@")) {
			return 1;
		} else {
			return first.getSortLetters().compareTo(second.getSortLetters());
		}
	}

}
