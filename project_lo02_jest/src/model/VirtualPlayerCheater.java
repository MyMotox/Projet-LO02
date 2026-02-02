package model;

import java.util.List;

/**
 * Stratégie pour un joueur virtuel tricheur qui optimise ses choix. Connaît les
 * valeurs de toutes les cartes (visible et cachées) et fait les meilleurs
 * choix.
 */
public class VirtualPlayerCheater implements Strategy {

	/**
	 * Crée une offre en fonction de l'index donné. i=1: carte 2 visible, carte 1
	 * cachée i=2: carte 1 visible, carte 2 cachée
	 * 
	 * @param i le type d'offre (1 ou 2)
	 * @param p le joueur qui fait l'offre
	 * @return l'offre créée, ou null si i n'est ni 1 ni 2
	 */
	@Override
	public Offer performOffer(int i, Player p) {

		if (i == 1) {
			return new Offer(p.getHand().get(1), p.getHand().get(0), p);

		}

		else if (i == 2) {
			return new Offer(p.getHand().get(0), p.getHand().get(1), p);
		}

		else {
			return null;
		}
	}

	/**
	 * Choisit la cible avec la carte de plus haute valeur (visible ou cachée).
	 * Triche en regardant toutes les cartes, même cachées.
	 * 
	 * @param p         le joueur qui fait le choix
	 * @param available la liste des offres disponibles
	 * @return le propriétaire de l'offre contenant la meilleure carte
	 */
	@Override
	public Player chooseTarget(Player p, List<Offer> available) {
		int index = 0;
		int max = 0;
		for (int i = 0; i < available.size(); i++) {
			if (available.get(i).getHiddenCard().getFaceValue() >= max) {
				max = available.get(i).getHiddenCard().getFaceValue();
				index = i;
			}
			if (available.get(i).getVisibleCard().getFaceValue() >= max) {
				max = available.get(i).getVisibleCard().getFaceValue();
				index = i;
			}
		}

		return available.get(index).getOwner();
	}

	/**
	 * Choisit toujours la carte avec la valeur la plus élevée. Triche en comparant
	 * les valeurs des cartes visible et cachée.
	 * 
	 * @param current le joueur qui fait le choix
	 * @param o       l'offre dans laquelle choisir
	 * @param stack   la pile de cartes (non utilisée)
	 * @return la carte avec la plus haute valeur
	 */
	@Override
	public Card choose(Player current, Offer o, List<Card> stack) {

		if (o.getVisibleCard().getFaceValue() >= o.getHiddenCard().getFaceValue()) {
			Card c = o.getVisibleCard();
			o.removeVisibleCard();
			return c;
		} else {
			Card c = o.getHiddenCard();
			o.removeHiddenCard();
			return c;
		}
	}

	/**
	 * Méthode vide - les joueurs virtuels n'affichent pas leur main.
	 * 
	 * @param p le joueur (non utilisé)
	 */
	@Override
	public void displayHand(Player p) {
	}

	/**
	 * Crée une offre en mettant la carte de plus faible valeur comme visible.
	 * Optimise pour garder la meilleure carte cachée.
	 * 
	 * @param p le joueur qui fait l'offre
	 * @return l'offre optimisée
	 */
	@Override
	public Offer offer(Player p) {
		if (p.getHand().get(0).getFaceValue() >= p.getHand().get(1).getFaceValue()) {
			return performOffer(1, p);
		} else {
			return performOffer(2, p);
		}

	}

}
