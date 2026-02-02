package model;

/**
 * Énumération des types de trophées disponibles dans Jest.
 * - HIGHEST : carte la plus haute d'une couleur
 * - LOWEST : carte la plus basse d'une couleur
 * - MAJORITY : majorité de cartes d'une couleur
 * - JOKER : possession d'un Joker
 * - BEST_JEST : meilleur score global
 * - BEST_JEST_NO_JOKER : meilleur score sans Joker
 */
public enum TrophyType {
	HIGHEST, LOWEST, MAJORITY, JOKER, BEST_JEST, BEST_JEST_NO_JOKER
}