package model;

import java.util.Comparator;
import java.util.List;

/**
 * Représente un trophée dans le jeu Jest. Chaque trophée a un type, une carte
 * source qui le définit, et peut être attribué à un joueur. Contient la logique
 * pour déterminer le gagnant selon le type de trophée.
 */
public class Trophy {

	private TrophyType type;
	private Card sourceCard;
	private Player owner;

	/**
	 * Construit un trophée avec un type et une carte source.
	 * 
	 * @param type   le type de trophée (HIGHEST, LOWEST, MAJORITY, etc.)
	 * @param source la carte qui a généré ce trophée
	 */
	public Trophy(TrophyType type, Card source) {
		this.type = type;
		this.sourceCard = source;
		this.owner = null;
	}

	/**
	 * Retourne le type du trophée.
	 * 
	 * @return le type de trophée
	 */
	public TrophyType getType() {
		return type;
	}

	/**
	 * Retourne le propriétaire actuel du trophée.
	 * 
	 * @return le joueur qui possède ce trophée, ou null si non attribué
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Attribue le trophée à un joueur.
	 * 
	 * @param p le joueur qui reçoit le trophée
	 */
	public void setOwner(Player p) {
		this.owner = p;
	}

	/**
	 * Retourne la carte qui a généré ce trophée.
	 * 
	 * @return la carte source du trophée
	 */
	public Card getSourceCard() {
		return sourceCard;
	}

	/**
	 * Trouve le joueur qui possède au moins un Joker dans son Jest.
	 * 
	 * @param players la liste des joueurs
	 * @return le premier joueur avec un Joker, ou null si aucun
	 */
	public Player findWinnerJoker(List<Player> players) {
		for (Player p : players) {
			boolean hasJ = p.getJest().stream().filter(c -> c != null) // Filtre les null
					.anyMatch(c -> c.getColor() == Color.JOKER);
			if (hasJ)
				return p;
		}
		return null;
	}

	/**
	 * Trouve le joueur avec le meilleur score Jest global.
	 * 
	 * @param players la liste des joueurs
	 * @param counter le compteur de score à utiliser
	 * @return le joueur avec le meilleur score, ou null si aucun
	 */
	public Player findWinnerBestJest(List<Player> players, ClassicCounter counter) {
		return players.stream().max(Comparator.comparingInt(counter::visit)).orElse(null);
	}

	/**
	 * Trouve le joueur avec le meilleur score Jest parmi ceux qui n'ont PAS de
	 * Joker.
	 * 
	 * @param players la liste des joueurs
	 * @param counter le compteur de score à utiliser
	 * @return le joueur avec le meilleur score sans Joker, ou null si aucun
	 */
	public Player findWinnerBestJestNoJoker(List<Player> players, ClassicCounter counter) {
		return players.stream().filter(p -> p.getJest().stream().filter(c -> c != null) // Filtre les null
				.noneMatch(c -> c.getColor() == Color.JOKER)).max(Comparator.comparingInt(counter::visit)).orElse(null);
	}

	/**
	 * Trouve le joueur qui possède la carte la plus haute de la couleur du trophée.
	 * 
	 * @param players la liste des joueurs
	 * @return le joueur avec la carte la plus haute, ou null si aucun
	 */
	public Player findWinnerHighest(List<Player> players) {
		Color suit = sourceCard.getColor();

		Player bestPlayer = null;
		Card best = null;

		for (Player p : players) {
			for (Card c : p.getJest()) {
				if (c != null && c.getColor() == suit) { // Vérifie que c n'est pas null
					if (best == null || c.getFaceValue() > best.getFaceValue()) {
						best = c;
						bestPlayer = p;
					}
				}
			}
		}
		return bestPlayer;
	}

	// LOWEST : carte la plus faible d'une couleur
	public Player findWinnerLowest(List<Player> players) {
		Color suit = sourceCard.getColor();

		Player bestPlayer = null;
		Card best = null;

		for (Player p : players) {
			for (Card c : p.getJest()) {
				if (c != null && c.getColor() == suit) { // Vérifie que c n'est pas null
					if (best == null || c.getFaceValue() < best.getFaceValue()) {
						best = c;
						bestPlayer = p;
					}
				}
			}
		}
		return bestPlayer;
	}

	// MAJORITY X : joueur avec le plus de cartes d'une valeur donnée
	public Player findWinnerMajority(List<Player> players) {
		int value = sourceCard.getFaceValue();

		Player winner = null;
		int max = 0;

		for (Player p : players) {
			int count = (int) p.getJest().stream().filter(c -> c != null) // Filtre les null
					.filter(c -> c.getFaceValue() == value).count();

			if (count > max) {
				max = count;
				winner = p;
			}
		}

		return winner;
	}

	@Override
	public String toString() {
		String ownerName = (owner == null ? "non attribué" : owner.getName());
		return type + " (" + sourceCard + ") -> " + ownerName;
	}
}