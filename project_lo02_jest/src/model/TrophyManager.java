package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire des trophées du jeu Jest.
 * Responsable de la création des trophées à partir des cartes initiales
 * et de l'attribution des trophées aux joueurs gagnants.
 * Délègue les règles de détermination des gagnants aux méthodes findWinner de Trophy.
 */
public class TrophyManager {
    
    private List<Trophy> trophies = new ArrayList<>();
    
    /**
     * Construit un gestionnaire de trophées à partir de 1 ou 2 cartes.
     * La deuxième carte peut être null pour les parties à 4 joueurs.
     * 
     * @param c1 la première carte de trophée (obligatoire)
     * @param c2 la deuxième carte de trophée (peut être null)
     */
    public TrophyManager(Card c1, Card c2) {
        Trophy t1 = TrophyCardResolver.resolve(c1);
        
        if (t1 != null)
            trophies.add(t1);
        
        //ne traite c2 que s'il n'est pas null (pour les parties à 3 joueurs)
        if (c2 != null) {
            Trophy t2 = TrophyCardResolver.resolve(c2);
            if (t2 != null)
                trophies.add(t2);
        }
    }
    
    /**
     * Retourne la liste des trophées gérés.
     * 
     * @return la liste des trophées
     */
    public List<Trophy> getTrophies() {
        return trophies;
    }
    
    /**
     * Attribue tous les trophées à leurs gagnants respectifs.
     * Utilise les méthodes findWinner appropriées selon le type de chaque trophée.
     * 
     * @param players la liste des joueurs participants
     * @param counter le compteur de score pour les trophées basés sur le score
     */
	public void assignAll(List<Player> players, ClassicCounter counter) {
        for (Trophy t : trophies) {
            Player winner = null;
            
            switch (t.getType()) {
            case HIGHEST:
                winner = t.findWinnerHighest(players);
                break;
            case LOWEST:
                winner = t.findWinnerLowest(players);
                break;
            case MAJORITY:
                winner = t.findWinnerMajority(players);
                break;
            case JOKER:
                winner = t.findWinnerJoker(players);
                break;
            case BEST_JEST:
                winner = t.findWinnerBestJest(players, counter);
                break;
            case BEST_JEST_NO_JOKER:
                winner = t.findWinnerBestJestNoJoker(players, counter);
                break;
            default:
                System.out.println("Trophée inconnu : " + t.getType());
                break;
            }
            
            if (winner != null) {
                t.setOwner(winner);
            }
        }
    }
}
