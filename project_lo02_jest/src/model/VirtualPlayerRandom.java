package model;

import java.util.List;
import java.util.Random;

/**
 * Stratégie pour un joueur virtuel qui fait des choix aléatoires. Utilise un
 * générateur de nombres aléatoires pour toutes ses décisions.
 */
public class VirtualPlayerRandom implements Strategy {

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
	 * Choisit toujours la première offre disponible.
	 * 
	 * @param p         le joueur qui fait le choix
	 * @param available la liste des offres disponibles
	 * @return le propriétaire de la première offre
	 */
	@Override
	public Player chooseTarget(Player p, List<Offer> available) {
		int index = 1;
		return available.get(index - 1).getOwner();
	}

	/**
	 * Choisit aléatoirement entre la carte visible ou cachée.
	 * 
	 * @param current le joueur qui fait le choix
	 * @param o       l'offre dans laquelle choisir
	 * @param stack   la pile de cartes (non utilisée)
	 * @return la carte choisie aléatoirement
	 */
	@Override
	public Card choose(Player current, Offer o, List<Card> stack) {
		Random randomNumbers = new Random();
		int index = randomNumbers.nextInt(2) + 1;

		if (index == 1) {
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
	 * Crée une offre aléatoire (type 1 ou 2).
	 * 
	 * @param p le joueur qui fait l'offre
	 * @return l'offre créée aléatoirement
	 */
	@Override
	public Offer offer(Player p) {
		Random randomNumbers = new Random();
		return performOffer(randomNumbers.nextInt(2) + 1, p);
	}

}
