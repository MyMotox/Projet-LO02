package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visiteur implémentant les règles de calcul de score classiques du jeu Jest.
 * Gère les règles spéciales pour le Joker, les Cœurs, les As et les Trophées.
 */
public class ClassicCounter implements Visitor {

	private List<Trophy> trophies;

	/**
	 * Construit un compteur avec une liste de trophées.
	 * 
	 * @param trophies la liste des trophées à prendre en compte pour le calcul de
	 *                 score
	 */
	public ClassicCounter(List<Trophy> trophies) {
		this.trophies = trophies;
	}

	/**
	 * Construit un compteur sans trophées.
	 */
	public ClassicCounter() {
		this(null);
	}

	/**
	 * Calcule le score d'un joueur selon les règles classiques de Jest. Prend en
	 * compte les règles spéciales : - Joker : +4 sans Cœurs, sinon inverse la
	 * valeur des Cœurs - Cœurs : valent leur valeur si 4 avec Joker, -valeur si <4
	 * avec Joker, 0 sinon - As : valent 5 points s'ils sont seuls de leur couleur,
	 * 1 sinon - Trophées : ajoutent des points bonus selon les conditions remplies
	 * 
	 * @param player le joueur dont calculer le score
	 * @return le score total du joueur
	 */
	@Override
	public int visit(Player player) {

		List<Card> jest = player.getJest();
		int score = 0;

		boolean hasJoker = false;
		int heartCount = 0;

		Map<Color, List<Card>> byColor = new HashMap<>();

		// ANALYSE DU JEST
		for (Card c : jest) {
			if (c == null)
				continue; // Ignorer les cartes null

			byColor.computeIfAbsent(c.getColor(), k -> new ArrayList<>()).add(c);

			if (c.getColor() == Color.JOKER) {
				hasJoker = true;
			}

			if (c.getColor() == Color.HEART) {
				heartCount++;
			}
		}

		// JOKER & HEARTS (traités en premier)
		if (hasJoker) {
			if (heartCount == 0) {
				score += 4;
			} else if (heartCount < 4) {
				// Hearts réduisent le score
				for (Card c : byColor.get(Color.HEART)) {
					score -= c.getFaceValue();
				}
			} else { // heartCount == 4
				// Hearts augmentent le score
				for (Card c : byColor.get(Color.HEART)) {
					score += c.getFaceValue();
				}
			}
		}
		// Si pas de Joker, les Hearts valent 0 (rien à faire)

		// ACES
		Map<Card, Integer> effectiveValues = new HashMap<>();
		for (Card c : jest) {
			if (c == null)
				continue; // Ignorer les cartes null

			if (c.getFaceValue() == 1) {
				int countInSuit = (int) jest.stream().filter(x -> x != null && x.getColor() == c.getColor()).count();
				effectiveValues.put(c, (countInSuit == 1) ? 5 : 1);
			} else {
				effectiveValues.put(c, c.getFaceValue());
			}
		}

		// SCORE DE BASE
		for (Card c : jest) {
			if (c == null)
				continue; // Ignorer les cartes null

			int effectiveValue = effectiveValues.get(c);

			switch (c.getColor()) {
			case SPADE, CLUB -> score += effectiveValue;
			case DIAMOND -> score -= effectiveValue;
			default -> {
			} // HEART & JOKER déjà traités
			}
		}

		// BLACK PAIRS
		// Regrouper les cartes noires
		Map<Integer, List<Card>> blackByEffectiveValue = new HashMap<>();

		for (Card c : jest) {
			if (c == null)
				continue;

			if (c.getColor() == Color.SPADE || c.getColor() == Color.CLUB) {
				int effVal = effectiveValues.get(c);
				blackByEffectiveValue.computeIfAbsent(effVal, k -> new ArrayList<>()).add(c);
			}

			if (c.getColor() == Color.BONUS) {
				score += 3;
			}

			if (c.getColor() == Color.GOLD) {
				score += 5;
			}

			if (c.getColor() == Color.MALUS) {
				score -= 3;
			}
		}

		// Compter les pairs : il faut un Spade et un Club de même valeur
		Set<Integer> pairedValues = new HashSet<>();
		for (int effVal : blackByEffectiveValue.keySet()) {
			List<Card> cardsWithThisValue = blackByEffectiveValue.get(effVal);

			boolean hasSpade = cardsWithThisValue.stream().anyMatch(card -> card.getColor() == Color.SPADE);
			boolean hasClub = cardsWithThisValue.stream().anyMatch(card -> card.getColor() == Color.CLUB);

			if (hasSpade && hasClub && !pairedValues.contains(effVal)) {
				score += 2;
				pairedValues.add(effVal);
			}
		}

		return score;
	}
}