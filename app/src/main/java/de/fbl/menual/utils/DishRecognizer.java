package de.fbl.menual.utils;

import com.google.android.gms.flags.impl.DataUtils;

public class DishRecognizer {

    public static String[] getDishes(String menuText)
    {
        String[] candidates = menuText.replace("\\n", ",").split(",");
        for(int i = 0; i<candidates.length;i++)
        {
            candidates[i] = removeNoiseAtStart(candidates[i]);
            candidates[i] = removeNonAlphabeticCharacters(candidates[i]);
            candidates[i] = wordsWithMinLength(candidates[i],3);
            candidates[i] = removeTooManyWordsInString(candidates[i], 4);


        }
        candidates = removeDuplicates(candidates);
        String[] newCandidates = removeEmptyStrings(candidates);
        return newCandidates;
    }
    public static String removeNoiseAtStart(String candidates)
    {
        int nonAlphabetPreceeding = 0;
        int firstLetter = 0;
        for(int j = 0; j<candidates.length();j++) //removes noise at the start
        {
            if((candidates.charAt(j) > 64 && candidates.charAt(j) < 91)||(candidates.charAt(j) > 96 &&candidates.charAt(j) < 123))
            {
                firstLetter = j;
                candidates = candidates.substring(j);
                j = candidates.length()+1; //breaks the for loop
            }
        }
        return candidates;
    }
    public static String removeNonAlphabeticCharacters(String candidate)
    {
        String[] wordCandidates = candidate.split(" ");
        int breakingpoint = -1;;
        boolean isKomma = false;
        for(int j = 0; j<wordCandidates.length;j++) //removes everything which is not a letter
        {
            for(int z=0; z<wordCandidates[j].length();z++)
            {
                if(!((wordCandidates[j].charAt(z) > 64 &&wordCandidates[j].charAt(z) < 91)||(wordCandidates[j].charAt(z) > 96 &&wordCandidates[j].charAt(z) < 123)))
                {
                    breakingpoint = j;
                    if(wordCandidates[j].charAt(z) == 44)
                        isKomma=true; //currently removes whole String, negotiable
                    z = wordCandidates[j].length()+1; //breaks the for loop

                }
            }
            if(breakingpoint > -1)
                j = wordCandidates.length+1; //breaks the for loop
        }
        String newCandidate = "";
        if(breakingpoint > -1)
        {
            for(int j = 0; j<breakingpoint;j++)
            {
                newCandidate += wordCandidates[j];
                newCandidate += " ";
            }
            candidate = newCandidate;
            if(isKomma)
                candidate = "";
        }
        return candidate;
    }
    public static String wordsWithMinLength(String candidate, int length)
    {
        if(candidate.length() >= length)
            return candidate;
        else
            return "";

    }
    public static String removeTooManyWordsInString(String candidate, int length)
    {
        int spaceCounter =0;
        for(int i = 0; i<candidate.length();i++)
        {
            if(candidate.charAt(i) == 32)
                spaceCounter++;
        }
        if(spaceCounter < length-1)
            return candidate;
        else
            return "";

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
                {
                    candidates[j] = "";
                }
            }
        }
        return candidates;
    }
}


