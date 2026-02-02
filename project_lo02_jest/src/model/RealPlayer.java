package model;

import java.util.List;
import java.util.Scanner;

/**
 * Stratégie pour un joueur humain en mode console. Utilise Scanner pour lire
 * les entrées utilisateur dans la console.
 */
public class RealPlayer implements Strategy {

	/**
	 * Crée une offre en fonction du choix du joueur. i=1: carte 2 visible, carte 1
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
	 * Demande au joueur de choisir une cible parmi les offres disponibles via la
	 * console.
	 * 
	 * @param p         le joueur qui fait le choix
	 * @param available la liste des offres disponibles
	 * @return le joueur cible choisi
	 */
	@Override
	public Player chooseTarget(Player p, List<Offer> available) {
		Scanner sc = new Scanner(System.in);
		System.out.println(p.getName() + " - Choisissez votre cible:");
		int index = sc.nextInt();
		return available.get(index - 1).getOwner();
	}

	/**
	 * Demande au joueur de choisir entre la carte visible (1) ou cachée (2) via la
	 * console.
	 * 
	 * @param current le joueur qui fait le choix
	 * @param o       l'offre dans laquelle choisir
	 * @param stack   la pile de cartes (non utilisée ici)
	 * @return la carte choisie
	 */
	@Override
	public Card choose(Player current, Offer o, List<Card> stack) {
		Scanner sc = new Scanner(System.in);
		System.out.println(current.getName() + " - Choisissez la carte visible (1) ou cachée (2)");
		int index = sc.nextInt();

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
	 * Affiche la main du joueur dans la console.
	 * 
	 * @param p le joueur dont afficher la main
	 */
	@Override
	public void displayHand(Player p) {
		System.out.println(p.getName() + " - votre main: ");
		System.out.println(p.getHand());
	}

	@Override
	public Offer offer(Player p) {
		System.out.println("Choisissez une carte à cacher (1) ou (2) :");
		java.util.Scanner sc = new java.util.Scanner(System.in);
		int index = sc.nextInt();
		return p.getStrategy().performOffer(index, p);
	}

}
