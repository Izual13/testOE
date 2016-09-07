package oe;


import openeye.oechem.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.stream.IntStream;

public class Main {

    private static final String SEPARATOR_SIGN = "_";
    private SecureRandom random = new SecureRandom();
    final String longString = new BigInteger(100, random).toString(10);

    public static void main(String[] args) {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        IntStream.range(0, 20000).forEach(value -> {
            new Main().start();
            System.out.println("Used Memory [" + value + "]:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        });

    }

    private void start() {
        OELibraryGen libgen = new OELibraryGen();
        libgen.Init("[O:1]=[C:2][Cl:3].[N:4]>>[O:1]=[C:2][Nh1:4]");
        libgen.SetExplicitHydrogens(false);
        libgen.SetValenceCorrection(true);

        OEGraphMol mol = new OEGraphMol();
        mol.SetTitle(longString);
        oechem.OESmilesToMol(mol, "CC(=O)Cl acetyl chloride");
        libgen.AddStartingMaterial(mol, 0);

        OEGraphMol mol1 = new OEGraphMol();
        mol1.SetTitle(longString);
        oechem.OESmilesToMol(mol1, "c1ccccc1C(=O)Cl benzoyl chloride");
        libgen.AddStartingMaterial(mol1, 0);

        OEGraphMol mol2 = new OEGraphMol();
        mol2.SetTitle(longString);
        oechem.OESmilesToMol(mol2, "NCC ethanamine");
        libgen.AddStartingMaterial(mol2, 1);

        OEGraphMol mol3 = new OEGraphMol();
        mol3.SetTitle(longString);
        oechem.OESmilesToMol(mol3, "OCCN 2-aminoethanol");
        libgen.AddStartingMaterial(mol3, 1);


        libgen.SetTitleSeparator(SEPARATOR_SIGN);
        for (OEMolBase product : libgen.GetProducts()) {
            oemolostream ofs = new oemolostream();
            ofs.SetFormat(OEFormat.MDL);
            ofs.openstring(false);
            product.ClearCoords();
            oechem.OEWriteMolecule(ofs, product);

            byte[] byteStructure = ofs.getByteArray();
            oechem.OECalculateMolecularWeight(product);
            ofs.close();

            String[] titles = product.GetTitle().split(SEPARATOR_SIGN);
        }
    }


}
