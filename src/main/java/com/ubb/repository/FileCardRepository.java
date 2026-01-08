package com.ubb.repository;

import com.ubb.domain.Duck;
import com.ubb.domain.card.Card;
import com.ubb.domain.card.TipCard;
import com.ubb.factory.CardFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository care gestionează cardurile de rațe și persistă datele în fișier.
 * Formatul din fișier:
 *   CARD,id,nume,tip,[id1,id2,id3]
 */
public class FileCardRepository extends AbstractRepository<Card<? extends Duck>> {
    private final String fileName;
    private final CardFactory cardFactory = new CardFactory();

    /** Map temporar: ID card → lista de ID-uri ale rațelor membre */
    private final Map<Long, List<Long>> membershipsMap = new HashMap<>();

    /**
     * Creează un repository bazat pe fișier pentru carduri.
     *
     * @param fileName calea către fișierul de carduri
     */
    public FileCardRepository(String fileName) {
        this.fileName = fileName;
        loadFromFile();
    }

    @Override
    protected Long getId(Card<? extends Duck> elem) {
        return elem.getId();
    }

    @Override
    public void add(Card<? extends Duck> elem) {
        super.add(elem);
        saveToFile();
    }

    @Override
    public void remove(Long id) {
        super.remove(id);
        saveToFile();
    }

    /**
     * Încarcă cardurile și listele de membri din fișier.
     * Formatul suportat:
     *   CARD,id,nume,tip,[id1,id2,id3]
     */
    private void loadFromFile() {
        getElems().clear();
        membershipsMap.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;

                Long id = Long.parseLong(parts[1].trim());
                String numeCard = parts[2].trim();
                String rest = parts[3].trim();


                int idxBracket = rest.indexOf('[');
                String tipStr = (idxBracket == -1)
                        ? rest.replace(",", "").trim()
                        : rest.substring(0, idxBracket).replace(",", "").trim();
                String membriStr = (idxBracket == -1)
                        ? "[]"
                        : rest.substring(idxBracket).trim();

                TipCard tip = TipCard.valueOf(tipStr);
                Card<? extends Duck> card = cardFactory.create(id, numeCard, tip);

                getElems().add(card);
                membershipsMap.put(id, parseMemberList(membriStr));
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea fișierului: " + e.getMessage());
        }
    }

    /**
     * Returnează mapul intern (ID card → lista ID-urilor membrilor)
     * pentru a reconstrui legăturile în CardService.
     */
    public Map<Long, List<Long>> getMembershipsMap() {
        return membershipsMap;
    }

    /**
     * Parsează stringul de membri din fișier în listă de ID-uri.
     * Exemplu: "[1, 2, 3]" → [1, 2, 3]
     */
    private List<Long> parseMemberList(String membriString) {
        String clean = membriString
                .replace("[", "")
                .replace("]", "")
                .trim();

        if (clean.isEmpty()) return new ArrayList<>();

        List<Long> membriIds = new ArrayList<>();
        for (String s : clean.split("\\s*,\\s*")) {
            try {
                membriIds.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {}
        }
        return membriIds;
    }

    /**
     * Salvează toate cardurile și membrii lor în fișier.
     */
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Card<? extends Duck> card : getElems()) {
                String membri = card.getMembri().isEmpty()
                        ? "[]"
                        : "[" + card.getMembri().stream()
                        .map(d -> d.getId().toString())
                        .reduce((a, b) -> a + "," + b)
                        .orElse("") + "]";

                pw.printf("CARD,%d,%s,%s,%s\n",
                        card.getId(),
                        card.getNumeCard(),
                        (card.getClass().getSimpleName().equals("SkyFlyers")) ? "FLYING" : "SWIMMING",
                        membri);
            }
        } catch (IOException e) {
            System.err.println("Eroare la scrierea fișierului: " + e.getMessage());
        }
    }


    public void update(Card<? extends Duck> elem) {
        super.update(elem);
        saveToFile();
    }

}
