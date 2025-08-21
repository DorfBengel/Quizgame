package gui.common;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Hilfsklasse für gemeinsame Validierungslogik.
 * Reduziert Code-Duplikation bei der Eingabevalidierung.
 */
public class ValidationHelper {
    
    /**
     * Validiert eine Eingabe mit einer Bedingung.
     * 
     * @param value Der zu validierende Wert
     * @param condition Die Validierungsbedingung
     * @param errorMessage Die Fehlermeldung bei ungültiger Eingabe
     * @return true wenn gültig, false wenn ungültig
     */
    public static <T> boolean validate(T value, Predicate<T> condition, String errorMessage) {
        if (!condition.test(value)) {
            System.err.println("Validierungsfehler: " + errorMessage);
            return false;
        }
        return true;
    }
    
    /**
     * Validiert, dass ein String nicht leer ist.
     */
    public static boolean validateNotEmpty(String value, String fieldName) {
        return validate(value, 
            s -> s != null && !s.trim().isEmpty(), 
            fieldName + " darf nicht leer sein");
    }
    
    /**
     * Validiert, dass ein String eine maximale Länge hat.
     */
    public static boolean validateMaxLength(String value, String fieldName, int maxLength) {
        return validate(value, 
            s -> s == null || s.trim().length() <= maxLength, 
            fieldName + " darf maximal " + maxLength + " Zeichen lang sein");
    }
    
    /**
     * Validiert, dass mindestens ein Element in einem Array ausgewählt ist.
     */
    public static boolean validateAtLeastOneSelected(boolean[] selections, String fieldName) {
        return validate(selections, 
            arr -> arr != null && hasAnyTrue(arr), 
            "Bitte wählen Sie mindestens eine " + fieldName + " aus");
    }
    
    private static boolean hasAnyTrue(boolean[] arr) {
        for (boolean b : arr) {
            if (b) return true;
        }
        return false;
    }
    
    /**
     * Validiert, dass mindestens eine richtige Antwort ausgewählt ist.
     */
    public static boolean validateAtLeastOneCorrect(boolean[] selections, String[] values) {
        if (selections == null || values == null) {
            return false;
        }
        
        boolean hasCorrect = false;
        for (int i = 0; i < Math.min(selections.length, values.length); i++) {
            if (selections[i] && values[i] != null && !values[i].trim().isEmpty()) {
                hasCorrect = true;
                break;
            }
        }
        
        if (!hasCorrect) {
            System.err.println("Validierungsfehler: Bitte markieren Sie mindestens eine Antwort als richtig");
            return false;
        }
        
        return true;
    }
    
    /**
     * Validiert, dass keine leere Antwort als richtig markiert ist.
     */
    public static boolean validateNoEmptyCorrect(boolean[] selections, String[] values) {
        if (selections == null || values == null) {
            return false;
        }
        
        for (int i = 0; i < Math.min(selections.length, values.length); i++) {
            if (selections[i] && (values[i] == null || values[i].trim().isEmpty())) {
                System.err.println("Validierungsfehler: Eine als richtig markierte Antwort ist leer");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Kombiniert mehrere Validierungen.
     */
    @SafeVarargs
    public static boolean validateAll(Supplier<Boolean>... validators) {
        for (Supplier<Boolean> validator : validators) {
            if (!validator.get()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Hilfsmethode zum Trimmen von Strings.
     */
    public static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
