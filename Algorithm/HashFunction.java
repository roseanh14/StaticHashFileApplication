package Algorithm;

public final class HashFunction {
    private HashFunction() {
    }

    /*
    Java obsahuje metodu hashCode ktera se da volat na stringu a mela by byt stejna jako tato implementace
    jen s tim rozdilem ze by si mela pamatovat vysledny hash pro dany klic, pokud by tedy na vstupu byl stejny klic, vypocet (iterace) by se nemusely
    opakovat (vysledek by se vzal z pameti), pouziti hashCode metody by melo byt nepatrne uspornejsi pro cpu, puvodni kod jsem zakomentoval,
    nechavam svoji upravu
    */
    public static int hash(String key, int primaryBlockCount) {
        /*
        int hash = 0;

        for (int i = 0; i < key.length(); i++) {
            hash = 31 * hash + key.charAt(i);
        }

        return Math.floorMod(hash, primaryBlockCount);
        */
        return Math.floorMod(key.hashCode(), primaryBlockCount);
    }
}