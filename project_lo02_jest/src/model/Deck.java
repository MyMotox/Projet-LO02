package model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Représente le paquet de cartes du jeu Jest. Gère la création, le mélange et
 * la distribution des cartes.
 */
public class Deck {

	// La pile de carte
	private LinkedList<Card> cards;

	/**
	 * Construit un paquet de cartes vide.
	 */
	public Deck() {
		cards = new LinkedList<>();
	}

	/**
	 * Initialise le paquet avec les cartes standard du jeu Jest. Crée 16 cartes
	 * normales (4 couleurs × valeurs 1-4), 1 Joker, et optionnellement les cartes
	 * d'extension (BONUS, MALUS, GOLD). Le paquet est automatiquement mélangé après
	 * initialisation.
	 * 
	 * @param extensionActive true pour inclure les cartes d'extension, false sinon
	 */
	public void initStandardDeck(boolean extensionActive) {
		cards.clear();
		// génération des 16 cartes normales : 4 couleurs x valeurs de 1 à 4
		for (int v = 1; v <= 4; v++) {
			for (Color c : Color.values()) {
				if (c != Color.JOKER && c != Color.BONUS && c != Color.MALUS && c != Color.GOLD) {
					cards.add(new Card(v, c));
				}
			}
		}

		// ajout manuel de l'unique Joker

		cards.add(new Card(0, Color.JOKER));

		if (extensionActive) {
			cards.add(new Card(2, Color.BONUS)); // ajout nouvelle carte si extension active.
			cards.add(new Card(3, Color.MALUS));
			cards.add(new Card(5, Color.GOLD));
		}

		// mélange du paquet

		shuffle();
	}

	/**
	 * Mélange aléatoirement les cartes du paquet.
	 */
	private void shuffle() {
		Collections.shuffle(cards);
	}

	/**
	 * Indique si le paquet est vide.
	 * 
	 * @return true si le paquet ne contient plus de cartes, false sinon
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}

	/**
	 * Tire et retire la première carte du paquet.
	 * 
	 * @return la première carte du paquet, ou null si le paquet est vide
	 */
	public Card draw() {
		if (cards.isEmpty())
			return null;
		return cards.removeFirst();
	}

	/**
	 * Ajoute une carte à la fin du paquet. Utilisé pour redistribuer des cartes ou
	 * ajouter des extensions.
	 * 
	 * @param c la carte à ajouter
	 */
	public void add(Card c) {
		cards.addLast(c);
	}

	/**
	 * Retourne le nombre de cartes restantes dans le paquet.
	 * 
	 * @return le nombre de cartes dans le paquet
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * Retourne la liste des cartes du paquet.
	 * 
	 * @return la liste des cartes
	 */
	public List<Card> getDeck() {
		return cards;

	}

}
