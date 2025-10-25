package lads.lads_website.backend_logic.LevelingPlanning;

public class CrystalCost {
    private String stellacrum;
    private int n;
    private int r;
    private int sr;

    public CrystalCost() {
        n = 0;
        r = 0;
        sr = 0;
    }

    public CrystalCost(String stellacrum) {
        this.stellacrum = stellacrum;
    }

    public CrystalCost(String stellacrum, int n, int r, int sr) {
        this.stellacrum = stellacrum;
        this.n = n;
        this.r = r;
        this.sr = sr;
    }

    public void add(CrystalCost crystalCost) {
        this.n += crystalCost.n;
        this.r += crystalCost.r;
        this.sr += crystalCost.sr;
    }

    public CrystalCost subtract(CrystalCost compare) {
        CrystalCost result = new CrystalCost(this.stellacrum);
        result.setN(Math.max(this.n - compare.getN(), 0));
        result.setR(Math.max(this.r - compare.getR(), 0));
        result.setSr(Math.max(this.sr - compare.getSr(), 0));
        return result;
    }

    public boolean equals(String stellacrum) {
        CrystalCost cc = new CrystalCost(stellacrum);
        return equals(cc);
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof CrystalCost instance) {
            retVal = instance.getStellacrum().equals(this.stellacrum);
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        return stellacrum.hashCode();
    }

    public String getStellacrum() {
        return stellacrum;
    }

    public void setStellacrum(String stellacrum) {
        this.stellacrum = stellacrum;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getSr() {
        return sr;
    }

    public void setSr(int sr) {
        this.sr = sr;
    }
}
