package nick;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.class_2378;
import sun.misc.Unsafe;

import java.awt.*;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public final class SettingBuilder {
    private static final int NO_PREDICATE = 0, SETTINGS_PREDICATE = 1, MODULE_PREDICATE = 2, COMBINED_PREDICATE = 3, LAMBDA_PREDICATE = 4;
    private static final int NUMBER_SETTING = 0, COLOR_SETTING = 1, BOOLEAN_SETTING = 2, ENUM_SETTING = 3, STRING_SETTING = 4, IDENTIFIER_SETTING = 5, STRING_SET_SETTING = 6, OBJECT_SET_SETTING = 7;
    private static final int CONSTANT_Integer = 0, CONSTANT_Float = 1, CONSTANT_Double = 2;

    private static final Map<String, List<Setting>> SETTINGS = new HashMap<>();
    private static final Map<Long, Setting> REGISTRY = new HashMap<>();
    private static final Class<?> SETTING_BASE;
    private static final Field _NAME, _DESCRIPTOR, _NAME_1, _DESC_1, B1, B2, B3, B4, B5, B6, _TI, O1, O2, O3, O4, O5, O6;
    private static final Field ENUM_SERIALIZER, COLOR_SERIALIZER;
    private static Field _SUFFIX, _C_B1, _C_B2, _REGISTRY, _SETTINGS;
    private static final Method _PREDICATE, _PREDICATE_1;
    private static final Unsafe UNSAFE;

    static {
        try {
            SETTING_BASE = Class.forName("me.mioclient.m$$mgj7lOGjbnPkgEMabAelHtQHhlNSKm4rB7epPogjFfmhcDSq5Doh6VS98DnS8jwTu7f191DoI4u6rdhssxqxTfuBNqXyIciF5");
            _NAME = SETTING_BASE.getSuperclass().getDeclaredField("m$$O9VdgwS9wXKP3mnKZ1KdZUVz9Vwj3LdbVtkSdiroNC62Xn4MV8zFUlZfcUhSVlBMxXP8Gewc5Zk9ECiiiMcWUuWhslR9gxNVP");
            _DESCRIPTOR = SETTING_BASE.getSuperclass().getDeclaredField("m$$3p7t07HFfQhhPeIzo51Vrab0JGrwd73SFr7EoQwxr1Mfye8wukWbKzL1WDMjaVxhZRFes7nnqRkUFwpugSRswrnx4a7uuTY3T");
            _NAME_1 = SETTING_BASE.getDeclaredField("m$$IyoRfaeG3yovvcHOxK7B1pYmorxgLs4zyp5HhBzkha6j3c0bEbrNzXy37iVwa1wq0Ix8baJna1XcbYJK0g0a5zGuCO27Foycw");
            _DESC_1 = SETTING_BASE.getDeclaredField("m$$OP6Dr3WWtrpSPLrSF0Na9qjHAU34ted0vBc1kys8PWvD0h7kAlrakpAI3aWW81LqaWElzL2BzXT86EjninJAOhz9Y7GPTddD5");
            B1 = SETTING_BASE.getDeclaredField("m$$QzyO75BWAHSyMpKH6o7M5DpYvhzz6EIFlyvNoxPBj1nFx2ShOGUecYvOWZjPNmLYde5ZzjmORxDuEj2OkaAUMxUHQnzDWXBVC");
            B2 = SETTING_BASE.getDeclaredField("m$$mnZejGOgCQYU2UIr4G6zPn0WzKvUzmyvVEY33Zan33t8dwg5r63MqjTB3q8GgNLbNWtL0Mh0CRAgyXiNYIy3BaHF3R6LysHr4");
            B3 = SETTING_BASE.getDeclaredField("m$$Acb9DDGp8gFXR1M59PbDsxkwOawJjPp3MoP1sL3EuyeLZ4m0Xcv82AuflY0qNm1HJwoHwJdjbTiy7ykdOfol8Z6A0LdGHcYw5");
            B4 = SETTING_BASE.getDeclaredField("m$$Acb9DDGp8gFXR1M59PbDsxkwOawJjPp3MoP1sL3EuyeLZ4m0Xcv82AuflY0qNm1HJwoHwJdjbTiy7ykdOfol8Z6A0LdGHcYw5");
            B5 = SETTING_BASE.getDeclaredField("m$$t7mZbcePeohYJO1LNWJPcGR0lHp6FltYaOu3iv5uMlUX60J7qfaOcvjQjr62okw7AoViZ5S0i8RFjWrQm3N6tolrq02uXkAC8");
            B6 = SETTING_BASE.getDeclaredField("m$$UKyfoCPadqRSMOhOjp5UyeDaZkSPWbc7C4pxfUgnJRcqEjG5PdOdWcfHMu1DersCrtZK9WSKjwpwXCiHRLbhTe06WrBEG4csd");
            _TI = SETTING_BASE.getDeclaredField("m$$hJejIIy30k9dvIqbtuR8qxsPXMM81zRi5PA6PBN1VI2tX7958oQjdWKfKZrxhCLDaQ8jO9PbTsDXXB5GsJgtLl2gKbBCnfwLF");
            _PREDICATE = SETTING_BASE.getDeclaredMethod("m$$zuygEX3RyGt3VlqW38nhmw8TCwaq1BeiOyLA3j6XAbvXIoeC3N2X8wU8ldfYs0j6jwg2Tjra3LXe7wUZLEEazqs6nC0X8W9F8", SETTING_BASE.arrayType());
            _PREDICATE_1 = SETTING_BASE.getDeclaredMethod("m$$VNUxFdmTkySgC55SUm8AuE43GkoYdtoEdVCYZ8rZWUZMa5Mi3mcSOtw0aV4xhExsIlyZfemYIoxfdc4tghJwIOe7MEV6NfTGW", Predicate.class, int.class);
            O1 = SETTING_BASE.getDeclaredField("m$$UD7ROvfds8eSLXTG6vFB5dMAk03jHsUBu1KaB53SssE88CDOeckX81eRT6PjPm2YrQ9SGBl2MktJN9NQCcSyIDqosm1lM6S7x");
            O2 = SETTING_BASE.getDeclaredField("m$$2IKqjxM5CCie1feTOyNzeD5vWgFFfvJQ7T4SMJyImuhiOyHyEyeD36Lvf9fAz6LUqaIZCIjmQqDutGRG8Qj9ZrxNuC8B5LQFl");
            O3 = SETTING_BASE.getDeclaredField("m$$dCZTfMRVfeYPXsQeXSgP82nfd4ik2eYhWMntsOvAPNzaKYWGiQ9Sqmfu4Y8s7YKzCxysFYeRLCLO0gQ6XwtJw62fTTTmt4ofQ");
            O4 = SETTING_BASE.getDeclaredField("m$$QWM5eF0K89PZVITccKq4B66vP2O9MptQwgh4OU62RUj6Y59uC3GGu7BNvNcQp3u89S0IBu0FJozIlm4pqgtBTVfAKFnTg5mnF");
            O5 = SETTING_BASE.getDeclaredField("m$$imIrh6eW4tGxtkMZGLEtSalH1H0P64VQc2l8GR9G3f854a1jkvYqEMV1zVkLHM08qzoqihcP4n55y5jaFxd8VgsmdF2SzZMQO");
            O6 = SETTING_BASE.getDeclaredField("m$$CuxPDbeLNAusVP7lqYtDMb84xFpfVpDCqKX0mYwKMg4JWoQwSF3fWsPlLY5qC1a61Q0bLiZTxRe5vHWbKsnSLINXdBbSPlgad");

            ENUM_SERIALIZER = Class.forName("me.mioclient.m$$Ls9RIwOD9TTRE3MpdbVXPuFDiWW4Q5x12iHpULh2HCc954cwwVX4QUml5gWiofVItBVvqzIkoRcnRd1OS8i0ZOKrSvkjd2WJR").getDeclaredField("m$$cr1cUwlXRwlij0FDPBwDumNZtQe5dZUYMwStzZlYNBu9dRJEQrtYU22mu3knmGhhSAobASlR6ckvjozWrz8lteaYivYVxYUry");
            COLOR_SERIALIZER = Class.forName("me.mioclient.m$$CJMBtkzetTALNFlOt42e4JEAl3OddaiftiuchJen5PkaOjV01NuV6QgHJa9S0NJleFdab3OrAOeanaqvXjpq4L2rr13aJDSVm").getDeclaredField("m$$IIZdnPWN4OUIzHUCS7SVLSgvpwboCyz161a2ICRwBI0V7TIFlu3BALprZ8VlzUH1pDnvoOtajj7XGQgVOSeIlUSoTFv8MFQqt");

            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Throwable _t) {
            throw new RuntimeException(_t);
        }
    }

    private static void additional() throws Throwable {
        if (_SETTINGS != null) return;

        _SUFFIX = Class.forName("me.mioclient.m$$bbeNkSQujnzcYBth4lKKzyLe0EkCLf6zjHcEMNTMYqdb6aTjDGp98ZxhWRq5geV7pHGfAWgyCh20GrHaP5bsZChxFRrYEKQ0q").getDeclaredField("m$$Siqxdw60dnxHxOD8uuwYiX8PVK89Zxq606yoos2T2hm2o8ThAcHt6SwQbcU63ih2Pt3VG9BWl8FAWK96c3J8pfnMWQwW1wzAb");

        final Class<?> _c = Class.forName("me.mioclient.m$$CJMBtkzetTALNFlOt42e4JEAl3OddaiftiuchJen5PkaOjV01NuV6QgHJa9S0NJleFdab3OrAOeanaqvXjpq4L2rr13aJDSVm");
        _C_B1 = _c.getDeclaredField("m$$FKzYq6WxUBITt44akNhEeqO8GUim57EVieKu1jHTcEm2TdtebYtfT8NxbtyKgt73I2tx9EixPiHwOs6dPaEbjXymaYxum6S86");
        _C_B2 = _c.getDeclaredField("m$$gYMc6Khfaf6sxpeK0US8NBX7CCuctT2ZE0Y9olbo9HiP36JdwWVmj3ZKZACYLNZsulb4vs7RDnk45fSzWIdVX6MMbS0cSgSBk");

        _REGISTRY = Class.forName("me.mioclient.m$$WBph0c7B9Yfm8iuQUN1x87mXXnM5VEpwOjDu2en4nUTP91vXRxsgLf1pJiQ9fPS3ViAA4Cx8ZT86kwAGMgboXtTgkfjN2VklC").getDeclaredField("m$$xQ8jgx5howJKGmMwgMbM6XY1HDJnKjLumWj5cbrlxmQsb1iu27roC6c5V6l60cyfcejJAI13OkEqUwU9B2XRuHuFcczhTmBIi");
        _SETTINGS = Class.forName("me.mioclient.m$$ZouiUFkx1xxfpBCvUWH4OdXb3qz16ogdTMDdm34jRdLOhrbTSxZZteaDPu1Hx66ZUuzeDqKBGzbTPxI2NAcrmz6IMFQwQtaGM").getDeclaredField("m$$1sOCcTvY9bKrHtctf4tJkRGMFNquAfFAGMwqcAIFd0rpqtzV2pTSoNAG8QQF3hbdZVOdsdVRRWcdbmlDGQ7YwLk1nxG3yGtjh");
    }

    private static final class Setting {
        public Object instance;
        public Data data;

        public int tag;
        public long mod;
        public String name;
        public long id;
        public String field;
        public String _klass;
        public String _name; // m$$O9VdgwS9wXKP3mnKZ1KdZUVz9Vwj3LdbVtkSdiroNC62Xn4MV8zFUlZfcUhSVlBMxXP8Gewc5Zk9ECiiiMcWUuWhslR9gxNVP
        public String _descriptor; // m$$3p7t07HFfQhhPeIzo51Vrab0JGrwd73SFr7EoQwxr1Mfye8wukWbKzL1WDMjaVxhZRFes7nnqRkUFwpugSRswrnx4a7uuTY3T
        public String _name_1; // m$$IyoRfaeG3yovvcHOxK7B1pYmorxgLs4zyp5HhBzkha6j3c0bEbrNzXy37iVwa1wq0Ix8baJna1XcbYJK0g0a5zGuCO27Foycw
        public String _desc_1; // m$$OP6Dr3WWtrpSPLrSF0Na9qjHAU34ted0vBc1kys8PWvD0h7kAlrakpAI3aWW81LqaWElzL2BzXT86EjninJAOhz9Y7GPTddD5
        public boolean b1; // m$$QzyO75BWAHSyMpKH6o7M5DpYvhzz6EIFlyvNoxPBj1nFx2ShOGUecYvOWZjPNmLYde5ZzjmORxDuEj2OkaAUMxUHQnzDWXBVC
        public boolean b2; // m$$mnZejGOgCQYU2UIr4G6zPn0WzKvUzmyvVEY33Zan33t8dwg5r63MqjTB3q8GgNLbNWtL0Mh0CRAgyXiNYIy3BaHF3R6LysHr4
        public boolean b3; // m$$iundE9ZEO8yjuOY1ZivHkqcAs5RAg0wVllkVa4AODnyFqfDKm4i4a2wOkFTmfCv1HvHI0IQGbqKHT8dT1vYIxcWfGc2MU8jk1
        public boolean b4; // m$$Acb9DDGp8gFXR1M59PbDsxkwOawJjPp3MoP1sL3EuyeLZ4m0Xcv82AuflY0qNm1HJwoHwJdjbTiy7ykdOfol8Z6A0LdGHcYw5
        public boolean b5; // m$$t7mZbcePeohYJO1LNWJPcGR0lHp6FltYaOu3iv5uMlUX60J7qfaOcvjQjr62okw7AoViZ5S0i8RFjWrQm3N6tolrq02uXkAC8
        public boolean b6; // m$$UKyfoCPadqRSMOhOjp5UyeDaZkSPWbc7C4pxfUgnJRcqEjG5PdOdWcfHMu1DersCrtZK9WSKjwpwXCiHRLbhTe06WrBEG4csd
        public Object _ti; // m$$hJejIIy30k9dvIqbtuR8qxsPXMM81zRi5PA6PBN1VI2tX7958oQjdWKfKZrxhCLDaQ8jO9PbTsDXXB5GsJgtLl2gKbBCnfwLF
        public List<PredicateInfo> predicates; // m$$o3GVghcU6F3QmYOBDUFSDkmEHBYZGBMEkAIn8BPP1MYFzmI6bFe7JHliFWPAQFjdIbhqA0wlbrhPHGqITQ4eL4FiAbNULWWFl

        public static void set(final Field field, final Object inst, final Object r) {
            UNSAFE.putObject(inst, UNSAFE.objectFieldOffset(field), r);
        }

        @SuppressWarnings("unchecked")
        public void build(final Object module) throws Throwable {
            if (instance != null) return;

            additional();

            final List<Object> settings = (List<Object>) _SETTINGS.get(module);

            if (field != null) {
                Field f;

                try {
                    f = module.getClass().getDeclaredField(field);
                } catch (Throwable _t) {
                    f = module.getClass().getSuperclass().getDeclaredField(field);
                }

                f.setAccessible(true);
                final Object o = f.get(module);
                if (o != null) {
                    instance = o;
                    return;
                }
            }

            final Object inst = instance = UNSAFE.allocateInstance(Class.forName(_klass.replace('/', '.')));
            set(_NAME, inst, _name);
            set(_DESCRIPTOR, inst, _descriptor);
            set(_NAME_1, inst, _name_1);
            set(_DESC_1, inst, _desc_1);
            B1.set(inst, b1);
            B2.set(inst, b2);
            B3.set(inst, b3);
            B4.set(inst, b4);
            B5.set(inst, b5);
            B6.set(inst, b6);
            _TI.set(inst, _ti);
            O1.set(inst, data.o1);
            O2.set(inst, data.o2);
            O3.set(inst, data.o3);
            O4.set(inst, data.o4);
            O5.set(inst, data.o5);
            O6.set(inst, data.o6);

            if (tag == ENUM_SETTING) set(ENUM_SERIALIZER, inst, ENUM_SERIALIZER.getType().getDeclaredConstructor(Class.class).newInstance(Class.forName(((EnumData)data).klass.replace('/', '.'))));
            else if (tag == COLOR_SETTING) set(COLOR_SERIALIZER, inst, COLOR_SERIALIZER.getType().getDeclaredConstructor().newInstance());

            for (PredicateInfo info : predicates) {
                if (info instanceof ModulePredicate mp) {
                    _PREDICATE_1.invoke(inst, (Predicate<?>) Class.forName(mp.klass.replace('/', '.')).getDeclaredConstructor(module.getClass()).newInstance(module), 0);
                } else if (info instanceof SettingPredicate sp) {
                    final Object[] arr = (Object[]) Array.newInstance(SETTING_BASE, sp.keys.length);

                    for (int i = 0, n = sp.keys.length; i < n; i++) {
                        final long l = sp.keys[i];
                        final Setting _s = REGISTRY.get(l);
                        _s.build(module);
                        arr[i] = _s.instance;
                    }

                    _PREDICATE.invoke(inst, (Object) arr);
                } else throw new RuntimeException();
            }

            if (data instanceof NumberData nd) _SUFFIX.set(inst, nd.suffix);
            else if (data instanceof ColorData cd) {
                _C_B1.set(inst, cd.b1);
                _C_B2.set(inst, cd.b2);
            } else if (data instanceof RegistryData rd) set(_REGISTRY, inst, rd.registry);

            if (field != null) {
                final Field f = module.getClass().getDeclaredField(field);
                f.setAccessible(true);
                f.set(module, inst);
                settings.add(instance);
            }
        }

        @Override
        public String toString() {
            return field;
        }
    }

    private static class PredicateInfo {}

    private static final class ModulePredicate extends PredicateInfo {
        public String klass;
    }

    private static final class SettingPredicate extends PredicateInfo {
        public long[] keys;
    }

    private static class Data {
        public Object o1; // m$$UD7ROvfds8eSLXTG6vFB5dMAk03jHsUBu1KaB53SssE88CDOeckX81eRT6PjPm2YrQ9SGBl2MktJN9NQCcSyIDqosm1lM6S7x
        public Object o2; // m$$2IKqjxM5CCie1feTOyNzeD5vWgFFfvJQ7T4SMJyImuhiOyHyEyeD36Lvf9fAz6LUqaIZCIjmQqDutGRG8Qj9ZrxNuC8B5LQFl
        public Object o3; // m$$dCZTfMRVfeYPXsQeXSgP82nfd4ik2eYhWMntsOvAPNzaKYWGiQ9Sqmfu4Y8s7YKzCxysFYeRLCLO0gQ6XwtJw62fTTTmt4ofQ
        public Object o4; // m$$QWM5eF0K89PZVITccKq4B66vP2O9MptQwgh4OU62RUj6Y59uC3GGu7BNvNcQp3u89S0IBu0FJozIlm4pqgtBTVfAKFnTg5mnF
        public Object o5; // m$$imIrh6eW4tGxtkMZGLEtSalH1H0P64VQc2l8GR9G3f854a1jkvYqEMV1zVkLHM08qzoqihcP4n55y5jaFxd8VgsmdF2SzZMQO
        public Object o6; // m$$CuxPDbeLNAusVP7lqYtDMb84xFpfVpDCqKX0mYwKMg4JWoQwSF3fWsPlLY5qC1a61Q0bLiZTxRe5vHWbKsnSLINXdBbSPlgad

        Data() {}
    }

    private static final class NumberData extends Data {
        public String suffix; // m$$Siqxdw60dnxHxOD8uuwYiX8PVK89Zxq606yoos2T2hm2o8ThAcHt6SwQbcU63ih2Pt3VG9BWl8FAWK96c3J8pfnMWQwW1wzAb
    }

    private static final class ColorData extends Data {
        public boolean b1; // m$$FKzYq6WxUBITt44akNhEeqO8GUim57EVieKu1jHTcEm2TdtebYtfT8NxbtyKgt73I2tx9EixPiHwOs6dPaEbjXymaYxum6S86
        public boolean b2; // m$$gYMc6Khfaf6sxpeK0US8NBX7CCuctT2ZE0Y9olbo9HiP36JdwWVmj3ZKZACYLNZsulb4vs7RDnk45fSzWIdVX6MMbS0cSgSBk
    }

    private static final class RegistryData extends Data {
        public class_2378<?> registry; // m$$xQ8jgx5howJKGmMwgMbM6XY1HDJnKjLumWj5cbrlxmQsb1iu27roC6c5V6l60cyfcejJAI13OkEqUwU9B2XRuHuFcczhTmBIi
    }

    public static final class EnumData extends Data {
        public String klass;
        public String t1, t2, t3, t4;
    }

    public static void read() {
        try (final DataInputStream dis = new DataInputStream(new FileInputStream("C:\\Users\\Nic\\Downloads\\Mio 2.2 Crack\\mio-main\\mio-main\\src\\main\\resources\\mio\\settings.bin"))) {
            while (dis.available() != 0) {
                final long mod = dis.readLong();
                final String name = dis.readUTF();

                for (int i = 0, n = dis.readInt(); i < n; i++) {
                    final Setting s = new Setting();

                    s.mod = mod;
                    s.name = name;

                    SETTINGS.computeIfAbsent(s.name, _k -> new ArrayList<>()).add(s);

                    s.id = dis.readLong();
                    REGISTRY.put(s.id, s);

                    s.field = dis.readBoolean() ? dis.readUTF() : null;

                    s._klass = dis.readUTF();
                    s._name = dis.readUTF();
                    s._descriptor = dis.readUTF();

                    s._name_1 = dis.readUTF();
                    s._desc_1 = dis.readUTF();

                    s.b1 = dis.readBoolean();
                    s.b2 = dis.readBoolean();
                    s.b3 = dis.readBoolean();
                    s.b4 = dis.readBoolean();
                    s.b5 = dis.readBoolean();
                    s.b6 = dis.readBoolean();


                    switch (s.tag = (int) dis.readByte()) {
                        case NUMBER_SETTING -> {
                            final NumberData data = new NumberData();

                            data.suffix = dis.readUTF();

                            switch ((int) dis.readByte()) {
                                case CONSTANT_Integer -> {
                                    data.o1 = dis.readInt();
                                    data.o2 = dis.readInt();
                                    data.o3 = dis.readInt();
                                    data.o4 = dis.readInt();
                                    data.o5 = dis.readInt();
                                    data.o6 = dis.readInt();
                                }
                                case CONSTANT_Float -> {
                                    data.o1 = dis.readFloat();
                                    data.o2 = dis.readFloat();
                                    data.o3 = dis.readFloat();
                                    data.o4 = dis.readFloat();
                                    data.o5 = dis.readFloat();
                                    data.o6 = dis.readFloat();
                                }
                                case CONSTANT_Double -> {
                                    data.o1 = dis.readDouble();
                                    data.o2 = dis.readDouble();
                                    data.o3 = dis.readDouble();
                                    data.o4 = dis.readDouble();
                                    data.o5 = dis.readDouble();
                                    data.o6 = dis.readDouble();
                                }
                            }

                            s.data = data;
                        }
                        case COLOR_SETTING -> {
                            final ColorData data = new ColorData();

                            data.o1 = new Color(dis.readInt(), true);
                            data.o2 = new Color(dis.readInt(), true);
                            data.o3 = new Color(dis.readInt(), true);
                            data.o6 = new Color(dis.readInt(), true);

                            data.b1 = dis.readBoolean();
                            data.b2 = dis.readBoolean();

                            s.data = data;
                        }
                        case BOOLEAN_SETTING -> {
                            final Data data = new Data();

                            data.o1 = dis.readBoolean();
                            data.o2 = dis.readBoolean();
                            data.o3 = dis.readBoolean();
                            data.o6 = dis.readBoolean();

                            s.data = data;
                        }
                        case ENUM_SETTING -> {
                            final EnumData data = new EnumData();

                            final Class<?> _enum = Class.forName(data.klass = dis.readUTF().replace('/', '.'));

                            final Field f1 = _enum.getDeclaredField(data.t1 = dis.readUTF()); f1.setAccessible(true);
                            final Field f2 = _enum.getDeclaredField(data.t2 = dis.readUTF()); f2.setAccessible(true);
                            final Field f3 = _enum.getDeclaredField(data.t3 = dis.readUTF()); f3.setAccessible(true);
                            final Field f4 = _enum.getDeclaredField(data.t4 = dis.readUTF()); f4.setAccessible(true);

                            data.o1 = f1.get(null);
                            data.o2 = f2.get(null);
                            data.o3 = f3.get(null);
                            data.o6 = f4.get(null);

                            s.data = data;
                        }
                        case STRING_SETTING -> {
                            final Data data = new Data();

                            data.o1 = dis.readUTF();
                            data.o2 = dis.readUTF();
                            data.o3 = dis.readUTF();
                            data.o6 = dis.readUTF();

                            s.data = data;
                        }
                        case IDENTIFIER_SETTING -> {
                            final Data data = new Data();

                            final Constructor<?> c = Class.forName("me.mioclient.m$$iCDFeSaDjD17bRG5DWKR9dip3UXUSnwjyMLfFat8j2znG8ByBzU8gvqBu1Mp6ncFg5JkCgDLf6QwE2S1NrodPpRwipXuMiKAp").getDeclaredConstructor(String.class, String.class);
                            c.setAccessible(true);

                            data.o1 = c.newInstance(dis.readUTF(), dis.readUTF());
                            data.o2 = c.newInstance(dis.readUTF(), dis.readUTF());
                            data.o3 = c.newInstance(dis.readUTF(), dis.readUTF());
                            data.o6 = c.newInstance(dis.readUTF(), dis.readUTF());

                            s.data = data;
                        }
                        case STRING_SET_SETTING -> {
                            final Data data = new Data();

                            final int size = dis.readInt();

                            data.o1 = new ObjectOpenHashSet<>(size);
                            data.o2 = new ObjectOpenHashSet<>(size);
                            data.o3 = new ObjectOpenHashSet<>(size);
                            data.o6 = new ObjectOpenHashSet<>(size);

                            s.data = data;
                        }
                        case OBJECT_SET_SETTING -> {
                            final RegistryData data = new RegistryData();

                            data.registry = (class_2378<?>) Class.forName(dis.readUTF()).getDeclaredField(dis.readUTF()).get(null);
                            data.o1 = readSet(dis);
                            data.o2 = readSet(dis);
                            data.o3 = readSet(dis);
                            data.o6 = readSet(dis);

                            s.data = data;
                        }
                    }

                    s._ti = dis.readBoolean() ? Class.forName("me.mioclient.m$$cifDyLjhDwVc7DsvicJov1z4w8LSeSc2Sfex0PAhnjIP2XhHgs2BkQSDAhNNuatlpTbaG9XEHrL4hgna1ccB8XvriWnVKoq0N").getDeclaredField(dis.readUTF()).get(null) : null;
                    s.predicates = new ArrayList<>();

                    switch ((int) dis.readByte()) {
                        case SETTINGS_PREDICATE -> s.predicates.add(readPredicate(dis, SETTINGS_PREDICATE));
                        case MODULE_PREDICATE -> s.predicates.add(readPredicate(dis, MODULE_PREDICATE));
                        case COMBINED_PREDICATE -> {
                            final int p1 = dis.readByte();
                            if (p1 == SETTINGS_PREDICATE || p1 == MODULE_PREDICATE) s.predicates.add(readPredicate(dis, p1));
                            final int p2 = dis.readByte();
                            if (p2 == SETTINGS_PREDICATE || p2 == MODULE_PREDICATE) s.predicates.add(readPredicate(dis, p2));
                        }
                    }
                }
            }
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    private static ObjectOpenHashSet<Object> readSet(final DataInputStream dis) throws Throwable {
        final ObjectOpenHashSet<Object> set = new ObjectOpenHashSet<>();
        for (int i = 0, n = dis.readInt(); i < n; i++) set.add(Class.forName(dis.readUTF()).getDeclaredField(dis.readUTF()).get(null));
        return set;
    }

    private static PredicateInfo readPredicate(final DataInputStream dis, final int type) throws Throwable {
        if (type == SETTINGS_PREDICATE) {
            final SettingPredicate predicate = new SettingPredicate();
            final long[] keys = predicate.keys = new long[dis.readInt()];
            for (int i = 0, n = keys.length; i < n; i++) keys[i] = dis.readLong();
            return predicate;
        } else if (type == MODULE_PREDICATE) {
            final ModulePredicate predicate = new ModulePredicate();
            predicate.klass = dis.readUTF();
            return predicate;
        } else throw new RuntimeException();
    }

    public static void apply(final Object module) throws Throwable {
        for (final Setting setting : Objects.requireNonNull(SETTINGS.get(module.getClass().getName().replace('.', '/')))) {
            setting.build(module);
        }
    }

    static {
        read();

        final List<Setting> settings = SETTINGS.get("me/mioclient/m$$qwqWzyCszuRg2E95uXVMRo5uOT52nW9izE922Hv3zqQJ8rd9MT28W4Os2lAog1zlGfmrhT8VOP3DrzSMAnwIBylr5iOdsLbLc");
        final Setting s = new Setting();
        s.field = "m$$WF6H8u1DNyQghkdcxFXY5FM9IOiGtsJqLrRMIKwDvWd2C42iDgWuBv8YP6ZVD5tfYeNZPvki8jgyZ6Duos9RsNqTCPApQGAqf";
        s.name = s._name_1 = s._name = "Snow";
        s._klass = "me/mioclient/m$$fiYtL4wupaCq0ZXNpLGDIXeBpdaxQjEl11X8wPEtDFV8ma5LDmu3LQ8PEZAffyJ1gzjlPx6Dnyql3o5hu2w19Zk62m9SBHgus";
        s._descriptor = s._desc_1 = "Snow";
        s.predicates = List.of();
        s.data = new Data();
        s.data.o1 = true;
        s.data.o2 = true;
        s.data.o3 = true;
        s.data.o6 = true;
        settings.add(s);
    }
}