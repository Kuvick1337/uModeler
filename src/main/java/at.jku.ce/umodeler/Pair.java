package at.jku.ce.umodeler;

public class Pair<FST, SND> {
    FST fst;
    SND snd;

    public Pair(FST fst, SND snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public FST getFst() {
        return fst;
    }

    public SND getSnd() {
        return snd;
    }
}
