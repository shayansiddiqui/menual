package de.fbl.menual.utils;
/**
 * @author Christopher Harth-Kitzerow
 *
 */
public class DishRecognizer {

    public static String[] getDishes(String menuText) {
        String[] candidates = menuText.replace("\\n", ",").split(",");
        for (int i = 0; i < candidates.length; i++) {
            candidates[i] = removeNoiseAtStart(candidates[i]);
            candidates[i] = removeNonAlphabeticCharacters(candidates[i]);
            candidates[i] = wordsWithMinLength(candidates[i], 3);
            candidates[i] = removeCommonpreFixes(candidates[i]);
            candidates[i] = removeTooManyWordsInString(candidates[i], 4);
            candidates[i] = removeSpacesAtTheEnd(candidates[i]);
        }
        candidates = removeCommonNonDishWords(candidates);
        candidates = removeDuplicates(candidates);
        String[] newCandidates = removeEmptyStrings(candidates);
        return newCandidates;
    }

    public static String removeNoiseAtStart(String candidates) {
        int nonAlphabetPreceeding = 0;
        int firstLetter = 0;
        for (int j = 0; j < candidates.length(); j++) //removes noise at the start
        {
            if ((candidates.charAt(j) > 64 && candidates.charAt(j) < 91) || (candidates.charAt(j) > 96 && candidates.charAt(j) < 123)) {
                firstLetter = j;
                candidates = candidates.substring(j);
                j = candidates.length() + 1; //breaks the for loop
            }
        }
        return candidates;
    }

    public static String removeNonAlphabeticCharacters(String candidate) {
        String[] wordCandidates = candidate.split(" ");
        int breakingpoint = -1;
        ;
        boolean isKomma = false;
        for (int j = 0; j < wordCandidates.length; j++) //removes everything which is not a letter
        {
            for (int z = 0; z < wordCandidates[j].length(); z++) {
                if (!((wordCandidates[j].charAt(z) > 64 && wordCandidates[j].charAt(z) < 91) || (wordCandidates[j].charAt(z) > 96 && wordCandidates[j].charAt(z) < 123))) {
                    breakingpoint = j;
                    if (wordCandidates[j].charAt(z) == 44)
                        isKomma = true; //currently removes whole String, negotiable
                    z = wordCandidates[j].length() + 1; //breaks the for loop

                }
            }
            if (breakingpoint > -1)
                j = wordCandidates.length + 1; //breaks the for loop
        }
        String newCandidate = "";
        if (breakingpoint > -1) {
            for (int j = 0; j < breakingpoint; j++) {
                newCandidate += wordCandidates[j];
                newCandidate += " ";
            }
            candidate = newCandidate;
            if (isKomma)
                candidate = "";
        }
        return candidate;
    }

    public static String wordsWithMinLength(String candidate, int length) {
        if (candidate.length() >= length)
            return candidate;
        else
            return "";

    }

    public static String removeTooManyWordsInString(String candidate, int length) {
        int spaceCounter = 0;
        for (int i = 0; i < candidate.length(); i++) {
            if (candidate.charAt(i) == 32)
                spaceCounter++;
        }
        if (spaceCounter < length - 1)
            return candidate;
        else
            return "";

    }

    public static String removeCommonpreFixes(String candidate) {
        String[] prefixes = {"fresh", "home", "made", "extra"};
        String[] candidateCheck = candidate.split(" ");
        String newCandidate = "";
        for (int i = 0; i < prefixes.length; i++) { //removes prefix and sets string back together
            if (candidateCheck[0].toLowerCase().contains(prefixes[i])) {
                for (int j = 1; j < candidateCheck.length; j++) {
                    newCandidate += candidateCheck[j];
                    if (j != candidateCheck.length - 1) {
                        newCandidate += " "; //adds spaces between words again to rebuild string
                    }
                }
                return removeCommonpreFixes(newCandidate); //Badass recursion to check for more prefixes
            }
        }
        return candidate;
    }
    public static String removeSpacesAtTheEnd(String candidate)
    {
        if(candidate.length() < 1)
        {
            return candidate;
        }
        if(candidate.charAt(candidate.length()-1) != 32)
        {
            return candidate;
        }
        else
        {
            String newCandidate = "";
            for (int i = 0; i < candidate.length()-1; i++) //copy of the candidate doesn't receive the space at the end
            {
                newCandidate+=candidate.charAt(i);
            }
            return removeSpacesAtTheEnd(newCandidate); //Badass recursion to remove multiple spaces after last word
        }
    }



    public static String[] removeCommonNonDishWords(String[] candidates)
    {
        String[] commonNonDishes = {"appeti","dessert","main","course","lunch","breakfast","dinner","ingeidient","menu","drink","side","start","meal","kid","child","reminder","serve","seasonal","everyday","day","select","combination","buffet","specials","general","favorite","recommendation","entrees"};
        for(int i = 0; i<candidates.length;i++)
        {
            for(int j = 0; j<commonNonDishes.length;j++)
            {
                if(candidates[i].toLowerCase().contains(commonNonDishes[j])) //If candidate containes these prhases it is probably not a dish
                {
                    candidates[i] = "";
                }
            }
        }
        return candidates;
    }
    public static String[] removeEmptyStrings(String[] candidates)
    {
        int dishCounter = 0;
        for(int i = 0; i<candidates.length;i++)
        {
            if(!candidates[i].isEmpty())
                dishCounter++;
        }
        int[] indices = new int[dishCounter];
        if(indices.length > 0)
        {
            for(int i = 0; i<candidates.length;i++)
            {
                if(!candidates[i].isEmpty())
                {
                    indices[indices.length-dishCounter] = i;
                    dishCounter--;
                }
            }
        }

        String[] newCandidates = new String[indices.length];
        for(int i = 0; i<indices.length;i++)
        {
            newCandidates[i] = candidates[indices[i]];
        }
        return newCandidates;
    }
    public static String[] removeDuplicates(String[] candidates)
    {
        for(int i =0; i < candidates.length;i++)
        {
            for(int j = i+1; j< candidates.length;j++)
            {
                if(candidates[j].equals(candidates[i]))
                    candidates[j] = "";
            }
        }
        return candidates;
    }

}


