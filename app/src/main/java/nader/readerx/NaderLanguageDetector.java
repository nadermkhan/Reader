package nader.readerx;

import java.util.*;

public class NaderLanguageDetector {
	
	public static String getMajorityLanguage(String input) {
		Map<String, Integer> counts = countLanguageCharacters(input);
		int maxCount = 0;
		String majorityLang = "Unknown";
		
		for (Map.Entry<String, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				majorityLang = entry.getKey();
			}
		}
		
		return majorityLang;
	}
	
	public static ArrayList<Map.Entry<String, Double>> getConfidenceScore(String input) {
		Map<String, Integer> counts = countLanguageCharacters(input);
		int total = counts.values().stream().mapToInt(Integer::intValue).sum();
		
		ArrayList<Map.Entry<String, Double>> confidenceList = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : counts.entrySet()) {
			double percentage = total == 0 ? 0.0 : (entry.getValue() * 100.0 / total);
			confidenceList.add(new AbstractMap.SimpleEntry<>(entry.getKey(), percentage));
		}
		
		return confidenceList;
	}
	
	private static Map<String, Integer> countLanguageCharacters(String input) {
		int english = 0, hindi = 0, urdu = 0, bangla = 0;
		
		for (char ch : input.toCharArray()) {
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
				english++;
				} else if (ch >= 0x0900 && ch <= 0x097F) {
				hindi++;
				} else if (ch >= 0x0600 && ch <= 0x06FF) {
				urdu++;
				} else if (ch >= 0x0980 && ch <= 0x09FF) {
				bangla++;
			}
		}
		
		Map<String, Integer> counts = new LinkedHashMap<>();
		counts.put("English", english);
		counts.put("Hindi", hindi);
		counts.put("Urdu", urdu);
		counts.put("Bangla", bangla);
		return counts;
	}
	
	// Example usage
	/*public static void main(String[] args) {
	String testInput = "यह एक sentence है जिसमें हिंदी और English दोनों हैं।";
	
	String majorityLang = getMajorityLanguage(testInput);
	ArrayList<Map.Entry<String, Double>> confidence = getConfidenceScore(testInput);
	
	System.out.println("Majority Language: " + majorityLang);
	System.out.println("Confidence Scores:");
	for (Map.Entry<String, Double> entry : confidence) {
		System.out.printf("  %s: %.2f%%\n", entry.getKey(), entry.getValue());
	}
}*/
}