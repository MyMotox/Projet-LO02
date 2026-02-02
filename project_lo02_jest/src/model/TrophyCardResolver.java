package model;

/**
 * Résolveur de trophées basé sur les cartes. Convertit une carte en trophée
 * selon sa couleur et sa valeur. Règles de conversion : - JOKER → BEST_JEST -
 * SPADE → HIGHEST - DIAMOND 4 → BEST_JEST_NO_JOKER - DIAMOND autre → MAJORITY -
 * HEART → JOKER - CLUB → BEST_JEST - BONUS → null (pas de trophée)
 */
public class TrophyCardResolver {

	/**
	 * Résout une carte en trophée correspondant.
	 * 
	 * @param c la carte à résoudre
	 * @return le trophée correspondant, ou null si la carte ne génère pas de
	 *         trophée
	 */
	public static Trophy resolve(Card c) {

		switch (c.getColor()) { // Sélection du trophée selon la couleur de la carte

		case JOKER:
			return new Trophy(TrophyType.BEST_JEST, c); // Joker : trophée Joker

		case SPADE:
			return new Trophy(TrophyType.HIGHEST, c); // Pique : carte la plus haute

		case DIAMOND:
			// Carreau : si valeur 4 : meilleur Jest sans Joker, sinon majorité
			if (c.getFaceValue() == 4)
				return new Trophy(TrophyType.BEST_JEST_NO_JOKER, c); // Carreau 4 : meilleur Jest sans Joker
			else
				return new Trophy(TrophyType.MAJORITY, c); // Autres carreaux : majorité

		case HEART:
			return new Trophy(TrophyType.JOKER, c); // Cœur : trophée Joker

		case CLUB:
			return new Trophy(TrophyType.BEST_JEST, c); // Trèfle : meilleur Jest

		case BONUS:
			return null; // Bonus n'est pas un trophée

		default:
			return null; // Couleur non reconnue : aucun trophée
		}
	}
}
