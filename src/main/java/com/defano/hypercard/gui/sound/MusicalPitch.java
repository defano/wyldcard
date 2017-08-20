package com.defano.hypercard.gui.sound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MusicalPitch {

    REST(0, 'r', 0),   // Represents a rest note (special case)

    C0(16.35, 'c', 0),
    Cs0_Db0(17.32, 'c', '#', 'd', 'b', 0),
    D0(18.35, 'd', 0),
    Ds0_Eb0(19.45, 'd', '#', 'e', 'b', 0),
    E0(20.6, 'e', 0),
    F0(21.83, 'f', 0),
    Fs0_Gb0(23.12, 'f', '#', 'g', 'b', 0),
    G0(24.5, 'g', 0),
    Gs0_Ab0(25.96, 'g', '#', 'a', 'b', 0),
    A0(27.5, 'a', 0),
    As0_Bb0(29.14, 'a', '#', 'b', 'b', 0),
    B0(30.87, 'b', 0),
    C1(32.7, 'c', 1),
    Cs1_Db1(34.65, 'c', '#', 'd', 'b', 1),
    D1(36.71, 'd', 1),
    Ds1_Eb1(38.89, 'd', '#', 'e', 'b', 1),
    E1(41.2, 'e', 1),
    F1(43.65, 'f', 1),
    Fs1_Gb1(46.25, 'f', '#', 'g', 'b', 1),
    G1(49, 'g', 1),
    Gs1_Ab1(51.91, 'g', '#', 'a', 'b', 1),
    A1(55, '1', 1),
    As1_Bb1(58.27, '1', '#', 'b', 'b', 1),
    B1(61.74, 'b', 1),
    C2(65.41, 'c', 2),
    Cs2_Db2(69.3, 'c', '#', 'd', 'b', 2),
    D2(73.42, 'd', 2),
    Ds2_Eb2(77.78, 'd', '#', 'e', 'b', 2),
    E2(82.41, 'e', 2),
    F2(87.31, 'f', 2),
    Fs2_Gb2(92.5, 'f', '#', 'g', 'b', 2),
    G2(98, 'g', 2),
    Gs2_Ab2(103.83, 'g', '#', 'a', 'b', 2),
    A2(110, 'a', 2),
    As2_Bb2(116.54, 'a', '#', 'b', 'b', 2),
    B2(123.47, 'b', 2),
    C3(130.81, 'c', 3),
    Cs3_Db3(138.59, 'c', '#', 'd', 'b', 3),
    D3(146.83, 'd', 3),
    Ds3_Eb3(155.56, 'd', '#', 'e', 'b', 3),
    E3(164.81, 'e', 3),
    F3(174.61, 'f', 3),
    Fs3_Gb3(185, 'f', '#', 'g', 'b', 3),
    G3(196, 'g', 3),
    Gs3_Ab3(207.65, 'g', '#', 'a', 'b', 3),
    A3(220, 'a', 3),
    As3_Bb3(233.08, 'a', '#', 'b', 'b', 3),
    B3(246.94, 'b', 3),
    C4(261.63, 'c', 4),
    Cs4_Db4(277.18, 'c', '#', 'd', 'b', 4),
    D4(293.66, 'd', 4),
    Ds4_Eb4(311.13, 'd', '#', 'e', 'b', 4),
    E4(329.63, 'e', 4),
    F4(349.23, 'f', 4),
    Fs4_Gb4(369.99, 'f', '#', 'g', 'b', 4),
    G4(392, 'g', 4),
    Gs4_Ab4(415.3, 'g', '#', 'a', 'b', 4),
    A4(440, 'a', 4),
    As4_Bb4(466.16, 'a', '#', 'b', 'b', 4),
    B4(493.88, 'b', 4),
    C5(523.25, 'c', 5),
    Cs5_Db5(554.37, 'c', '#', 'd', 'b', 5),
    D5(587.33, 'd', 5),
    Ds5_Eb5(622.25, 'd', '#', 'e', 'b', 5),
    E5(659.25, 'e', 5),
    F5(698.46, 'f', 5),
    Fs5_Gb5(739.99, 'f', '#', 'g', 'b', 5),
    G5(783.99, 'g', 5),
    Gs5_Ab5(830.61, 'g', '#', 'a', 'b', 5),
    A5(880, 'a', 5),
    As5_Bb5(932.33, 'a', '#', 'b', 'b', 5),
    B5(987.77, 'b', 5),
    C6(1046.5, 'c', 6),
    Cs6_Db6(1108.73, 'c', '#'),
    D6(1174.66, 'd', '6'),
    Ds6_Eb6(1244.51, 'd', '#', 'e', 'b', 6),
    E6(1318.51, 'e', 6),
    F6(1396.91, 'f', 6),
    Fs6_Gb6(1479.98, 'f', '#', 'g', 'b', 6),
    G6(1567.98, 'g', 6),
    Gs6_Ab6(1661.22, 'g', '#', 'a', 'b', 6),
    A6(1760, 'a', 6),
    As6_Bb6(1864.66, 'a', '#', 'b', 'b', 6),
    B6(1975.53, 'b', 6),
    C7(2093, 'c', 7),
    Cs7_Db7(2217.46, 'c', '#', 'd', 'b', 7),
    D7(2349.32, 'd', 7),
    Ds7_Eb7(2489.02, 'd', '#', 'e', 'b', 7),
    E7(2637.02, 'e', 7),
    F7(2793.83, 'f', 7),
    Fs7_Gb7(2959.96, 'f', '#', 'g', 'b', 7),
    G7(3135.96, 'g', 7),
    Gs7_Ab7(3322.44, 'g', '#', 'a', 'b', 7),
    A7(3520, 'a', 7),
    As7_Bb7(3729.31, 'a', '#', 'b', 'b', 7),
    B7(3951.07, 'b', 7),
    C8(4186.01, 'c', 8),
    Cs8_Db8(4434.92, 'c', '#', 'd', 'b', 8),
    D8(4698.63, 'd', 8),
    Ds8_Eb8(4978.03, 'd', '#', 'e', 'b', 8),
    E8(5274.04, 'e', 8),
    F8(5587.65, 'f', 8),
    Fs8_Gb8(5919.91, 'f', '#', 'g', 'b', 8),
    G8(6271.93, 'g', 8),
    Gs8_Ab8(6644.88, 'g', '#', 'a', 'b', 8),
    A8(7040, 'a', 8),
    As8_Bb8(7458.62, 'a', '#', 'b', 'b', 8),
    B8(7902.13, 'b', 8);

    private final double frequency;
    private final List<Character> name;
    private final List<Character> accidental;
    private final int octave;

    MusicalPitch(double frequency, char name, int octave) {
        this(frequency, Collections.singletonList(name), Collections.singletonList('-'), octave);
    }

    MusicalPitch(double frequency, char name1, char accidental1, char name2, char accidental2, int octave) {
        this(frequency, Arrays.asList(name1, name2), Arrays.asList(accidental1, accidental2), octave);
    }

    MusicalPitch(double frequency, List<Character> name, List<Character> accidental, int octave) {
        this.frequency = frequency;
        this.name = name;
        this.accidental = accidental;
        this.octave = octave;
    }

    public int getOctave() {
        return octave;
    }

    public char getName() {
        return name.get(0);
    }

    public char getAccidental() {
        return accidental.get(0);
    }

    public double getFrequenceAdjustment(MusicalPitch from) {
        return frequency / from.frequency;
    }

    public static MusicalPitch of(char name, char accidental, int octave) {

        // Special case: Rest note (has no accidental or octave)
        if (name == 'r') {
            return REST;
        }

        for (MusicalPitch thisFreq : values()) {
            if (thisFreq.name.contains(name) && thisFreq.accidental.contains(accidental) && thisFreq.octave == octave) {
                return thisFreq;
            }
        }

        throw new IllegalArgumentException("No musical frequency represented by name=" + name + " accidental=" + accidental + " octave=" + octave);
    }

}
