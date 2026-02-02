package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un joueur dans le jeu Jest. Chaque joueur possède une main de
 * cartes, une pile Jest, une stratégie de jeu et un état indiquant s'il a joué
 * ce tour.
 */
public class Player implements Visitable {

	private String name;
	private List<Card> hand = new ArrayList<>();
	private List<Card> jest = new ArrayList<>();
	private boolean hasPlayed = false;
	private Strategy strategy;

	/**
	 * Construit un joueur avec un nom et une stratégie de jeu.
	 * 
	 * @param name le nom du joueur
	 * @param s    la stratégie de jeu du joueur (humain ou IA)
	 */
	public Player(String name, Strategy s) {
		this.name = name;
		this.strategy = s;
	}

	/**
	 * Retourne ce joueur (utilisé pour compatibilité).
	 * 
	 * @return ce joueur
	 */
	public Player getPlayer() {
		return this;
	}

	/**
	 * Retourne la stratégie de jeu de ce joueur.
	 * 
	 * @return la stratégie du joueur
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	/**
	 * Retourne le nom du joueur.
	 * 
	 * @return le nom du joueur
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retourne la main du joueur.
	 * 
	 * @return la liste des cartes dans la main du joueur
	 */
	public List<Card> getHand() {
		return hand;
	}

	/**
	 * Retourne la pile Jest du joueur.
	 * 
	 * @return la liste des cartes dans le Jest du joueur
	 */
	public List<Card> getJest() {
		return jest;
	}

	/**
	 * Ajoute une carte à la main du joueur.
	 * 
	 * @param c la carte à ajouter
	 */
	public void toHand(Card c) {
		hand.add(c);
	}

	/**
	 * Ajoute une carte à la pile Jest du joueur.
	 * 
	 * @param c la carte à ajouter au Jest
	 */
	public void toJest(Card c) {
		jest.add(c);
	}

	/**
	 * Indique si le joueur a déjà joué ce tour.
	 * 
	 * @return true si le joueur a joué, false sinon
	 */
	public boolean hasPlayed() {
		return hasPlayed;
	}

	/**
	 * Marque le joueur comme ayant joué ce tour.
	 */
	public void SetHasPlayed() {
		hasPlayed = true;
	}

	/**
	 * Réinitialise le joueur comme n'ayant pas encore joué ce tour.
	 */
	public void SetHasNotPlayed() {
		hasPlayed = false;
	}

	/**
	 * Accepte un visiteur pour le pattern Visitor (calcul de score).
	 * 
	 * @param visitor le visiteur à accepter
	 * @return le score calculé par le visiteur
	 */
	@Override
	public int accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Recherche et retourne l'offre appartenant à ce joueur dans une liste
	 * d'offres.
	 * 
	 * @param offers la liste des offres à parcourir
	 * @return l'offre de ce joueur, ou null si aucune offre n'appartient à ce
	 *         joueur
	 */
	public Offer getOffer(List<Offer> offers) {
		Offer OfferCurrent = null;
		for (Offer o : offers) {
			if (o.getOwner() == this) {
				OfferCurrent = o;
			}
		}
		return OfferCurrent;

	}

	@Override
	public String toString() {
		return name;
	}

}