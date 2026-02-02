package model;

/**
 * Représente une offre dans le jeu Jest. Chaque offre contient une carte
 * visible, une carte cachée et le joueur propriétaire. Les autres joueurs
 * peuvent choisir l'une des deux cartes lors de la résolution des offres.
 */
public class Offer {

	private Card visible; // Carte face visible
	private Card hidden; // Carte face cachée
	private Player owner; // Joueur qui fait l’offre

	/**
	 * Construit une nouvelle offre avec une carte visible, une carte cachée et un
	 * propriétaire.
	 * 
	 * @param visible la carte face visible de l'offre
	 * @param hidden  la carte face cachée de l'offre
	 * @param owner   le joueur qui propose cette offre
	 */
	public Offer(Card visible, Card hidden, Player owner) {
		this.visible = visible;
		this.hidden = hidden;
		this.owner = owner;
	}

	/**
	 * Retourne la carte visible de l'offre.
	 * 
	 * @return la carte visible, ou null si elle a été prise
	 */
	public Card getVisibleCard() {
		return visible;
	}

	/**
	 * Retourne la carte cachée de l'offre.
	 * 
	 * @return la carte cachée, ou null si elle a été prise
	 */
	public Card getHiddenCard() {
		return hidden;
	}

	/**
	 * Retourne le joueur propriétaire de cette offre.
	 * 
	 * @return le joueur qui a créé cette offre
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Retire la carte visible de l'offre (lorsqu'elle est choisie par un joueur).
	 */
	public void removeVisibleCard() {
		this.visible = null;
	}

	/**
	 * Retire la carte cachée de l'offre (lorsqu'elle est choisie par un joueur).
	 */
	public void removeHiddenCard() {
		this.hidden = null;
	}

	/**
	 * Retourne la carte restante dans l'offre après qu'une carte ait été choisie.
	 * 
	 * @return la carte restante (visible ou cachée), ou null si l'offre est vide ou
	 *         complète
	 */
	public Card getRemainingCard() {
		if (visible != null && hidden == null)
			return visible;

		if (hidden != null && visible == null)
			return hidden;

		return null; // offre vide ou complète → pas de carte restante
	}

	/**
	 * Vide complètement l'offre en retirant les deux cartes. Utilisé en fin de
	 * round pour nettoyer les offres.
	 */
	public void clearOffer() {
		this.visible = null;
		this.hidden = null;
	}

	@Override
	public String toString() {
		return owner.getName() + " [VISIBLE=" + visible + ", HIDDEN=" + (hidden != null ? "???" : "null") + "]";
	}
}