package model;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import view.GameViewInterface;

/**
 * Stratégie pour un joueur humain interactif avec support multi-vues. Permet de
 * gérer simultanément une vue console et une vue GUI. Utilise une file
 * d'attente bloquante pour recevoir les réponses des vues.
 */
public class InteractivePlayerStrategy implements Strategy {

	private GameViewInterface primaryView;
	private GameViewInterface secondaryView;
	private BlockingQueue<Integer> responseQueue;

	/**
	 * Construit une stratégie interactive avec une vue principale.
	 * 
	 * @param primaryView la vue principale pour les interactions
	 */
	public InteractivePlayerStrategy(GameViewInterface primaryView) {
		this.primaryView = primaryView;
		this.responseQueue = new LinkedBlockingQueue<>();
	}

	/**
	 * Définit une vue secondaire optionnelle pour les interactions.
	 * 
	 * @param secondaryView la vue secondaire (par exemple GUI si primaryView est
	 *                      Console)
	 */
	public void setSecondaryView(GameViewInterface secondaryView) {
		this.secondaryView = secondaryView;
	}

	/**
	 * Change la vue principale active.
	 * 
	 * @param view la nouvelle vue principale
	 */
	public void setActiveView(GameViewInterface view) {
		this.primaryView = view;
	}

	/**
	 * Reçoit une réponse depuis n'importe quelle vue active. Place la réponse dans
	 * la file d'attente pour traitement.
	 * 
	 * @param response la réponse fournie par une vue
	 */
	public void provideResponse(int response) {
		responseQueue.offer(response);
	}

	@Override
	public Offer performOffer(int i, Player p) {
		if (i == 1) {
			return new Offer(p.getHand().get(1), p.getHand().get(0), p);
		} else if (i == 2) {
			return new Offer(p.getHand().get(0), p.getHand().get(1), p);
		}
		return null;
	}

	@Override
	public Offer offer(Player p) {
		displayHand(p);
		responseQueue.clear();

// Lancer les demandes dans les deux vues en parallèle
		Thread t1 = new Thread(() -> primaryView.askForOffer(p));
		t1.start();

		if (secondaryView != null) {
			Thread t2 = new Thread(() -> secondaryView.askForOffer(p));
			t2.start();
		}

// Attendre la première réponse
		int choice = waitForResponse();
		return performOffer(choice, p);
	}

	@Override
	public Player chooseTarget(Player p, List<Offer> available) {
		responseQueue.clear();

// Lancer les demandes dans les deux vues en parallèle
		Thread t1 = new Thread(() -> primaryView.askForTarget(p, available));
		t1.start();

		if (secondaryView != null) {
			Thread t2 = new Thread(() -> secondaryView.askForTarget(p, available));
			t2.start();
		}

// Attendre la première réponse
		int index = waitForResponse();
		if (index < 1 || index > available.size()) {
			return null;
		}
		return available.get(index - 1).getOwner();
	}

	@Override
	public Card choose(Player current, Offer o, List<Card> stack) {
		responseQueue.clear();

// Lancer les demandes dans les deux vues en parallèle
		Thread t1 = new Thread(() -> primaryView.askForCard(current, o));
		t1.start();

		if (secondaryView != null) {
			Thread t2 = new Thread(() -> secondaryView.askForCard(current, o));
			t2.start();
		}

// Attendre la première réponse
		int choice = waitForResponse();

		if (choice == 1 && o.getVisibleCard() != null) {
			Card c = o.getVisibleCard();
			o.removeVisibleCard();
			return c;
		} else if (choice == 2 && o.getHiddenCard() != null) {
			Card c = o.getHiddenCard();
			o.removeHiddenCard();
			return c;
		}

		return null;
	}

	private int waitForResponse() {
		try {
			Integer response = responseQueue.poll(60, TimeUnit.SECONDS);
			return response != null ? response : 1;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public void displayHand(Player p) {
// Les vues affichent déjà
	}
}
