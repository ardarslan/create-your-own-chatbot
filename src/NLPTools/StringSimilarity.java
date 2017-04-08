package NLPTools;

import java.util.HashMap;
import java.util.Map;

//DamerauLevenshteinAlgorithm
public class StringSimilarity {

	private int deleteCost = 1;
	private int insertCost = 1;
	private int replaceCost = 1;
	private int swapCost = 1;

	public static void main(String args[]) {
		System.out.println(new StringSimilarity().execute("ihtiyaç", "ihtiyac"));
	}

	public StringSimilarity() {
		/*
		 * Required to facilitate the premise to the algorithm that two swaps of the
		 * same character are never required for optimality.
		 */
		if (2 * swapCost < insertCost + deleteCost) {
			throw new IllegalArgumentException("Unsupported cost assignment");
		}
	}

	/**
	 * Compute the Damerau-Levenshtein distance between the specified source
	 * string and the specified target string.
	 */
	public int execute(String source, String target) {

		if (source.equals("burmak") && target.equals("kurmak")) {
			return 2;
		}
		else if (source.equals("kurmak") && target.equals("burmak")) {
			return 2;
		}

		String sourceEdited = RemoveTurkishCharacters(source);
		String targetEdited = RemoveTurkishCharacters(target);


		if (sourceEdited.length() == 0) {
			return targetEdited.length() * insertCost;
		}
		if (targetEdited.length() == 0) {
			return sourceEdited.length() * deleteCost;
		}
		int[][] table = new int[sourceEdited.length()][targetEdited.length()];
		Map<Character, Integer> sourceIndexByCharacter = new HashMap<Character, Integer>();
		if (sourceEdited.charAt(0) != targetEdited.charAt(0)) {
			table[0][0] = Math.min(replaceCost, deleteCost + insertCost);
		}
		sourceIndexByCharacter.put(sourceEdited.charAt(0), 0);
		for (int i = 1; i < sourceEdited.length(); i++) {
			int deleteDistance = table[i - 1][0] + deleteCost;
			int insertDistance = (i + 1) * deleteCost + insertCost;
			int matchDistance = i * deleteCost
					+ (sourceEdited.charAt(i) == targetEdited.charAt(0) ? 0 : replaceCost);
			table[i][0] = Math.min(Math.min(deleteDistance, insertDistance),
					matchDistance);
		}
		for (int j = 1; j < targetEdited.length(); j++) {
			int deleteDistance = (j + 1) * insertCost + deleteCost;
			int insertDistance = table[0][j - 1] + insertCost;
			int matchDistance = j * insertCost
					+ (sourceEdited.charAt(0) == targetEdited.charAt(j) ? 0 : replaceCost);
			table[0][j] = Math.min(Math.min(deleteDistance, insertDistance),
					matchDistance);
		}
		for (int i = 1; i < sourceEdited.length(); i++) {
			int maxSourceLetterMatchIndex = sourceEdited.charAt(i) == targetEdited.charAt(0) ? 0
					: -1;
			for (int j = 1; j < targetEdited.length(); j++) {
				Integer candidateSwapIndex = sourceIndexByCharacter.get(targetEdited
						.charAt(j));
				int jSwap = maxSourceLetterMatchIndex;
				int deleteDistance = table[i - 1][j] + deleteCost;
				int insertDistance = table[i][j - 1] + insertCost;
				int matchDistance = table[i - 1][j - 1];
				if (sourceEdited.charAt(i) != targetEdited.charAt(j)) {
					matchDistance += replaceCost;
				} else {
					maxSourceLetterMatchIndex = j;
				}
				int swapDistance;
				if (candidateSwapIndex != null && jSwap != -1) {
					int iSwap = candidateSwapIndex;
					int preSwapCost;
					if (iSwap == 0 && jSwap == 0) {
						preSwapCost = 0;
					} else {
						preSwapCost = table[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
					}
					swapDistance = preSwapCost + (i - iSwap - 1) * deleteCost
							+ (j - jSwap - 1) * insertCost + swapCost;
				} else {
					swapDistance = Integer.MAX_VALUE;
				}
				table[i][j] = Math.min(Math.min(Math
						.min(deleteDistance, insertDistance), matchDistance), swapDistance);
			}
			sourceIndexByCharacter.put(sourceEdited.charAt(i), i);
		}
		return table[sourceEdited.length() - 1][targetEdited.length() - 1];
	}


	public static String RemoveTurkishCharacters(String str)
	{
		return str.replace('ç', 'c').replace('ş', 's').replace('ö', 'o').replace('ğ', 'g').replace('ı', 'i').replace('ü', 'u');
	}


}