package nick;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MioClassLoader extends SecureClassLoader {
    private static final Map<Long, byte[]> DATA = new HashMap<>() {
        {
            put(-1156854636071942612L, read("mio/-1156854636071942612.bin"));
            put(-202295177518396515L, read("mio/-202295177518396515.bin"));
            put(-2037366403957045892L, read("mio/-2037366403957045892.bin"));
            put(-2051135075305702010L, read("mio/-2051135075305702010.bin"));
            put(-2236490846758828587L, read("mio/-2236490846758828587.bin"));
            put(-2587742845299202020L, read("mio/-2587742845299202020.bin"));
            put(-3196439585737283335L, read("mio/-3196439585737283335.bin"));
            put(-3477212311278526151L, read("mio/-3477212311278526151.bin"));
            put(-3566755497116400425L, read("mio/-3566755497116400425.bin"));
            put(-4045037417574934788L, read("mio/-4045037417574934788.bin"));
            put(-5184565472326166436L, read("mio/-5184565472326166436.bin"));
            put(-5625967054364314949L, read("mio/-5625967054364314949.bin"));
            put(-6862787127220354815L, read("mio/-6862787127220354815.bin"));
            put(-7023810658846360874L, read("mio/-7023810658846360874.bin"));
            put(-7090278656847170995L, read("mio/-7090278656847170995.bin"));
            put(-7183123058599273620L, read("mio/-7183123058599273620.bin"));
            put(-7341433280440695994L, read("mio/-7341433280440695994.bin"));
            put(-7680579496922721047L, read("mio/-7680579496922721047.bin"));
            put(-7964734080343971309L, read("mio/-7964734080343971309.bin"));
            put(-8099616702159452490L, read("mio/-8099616702159452490.bin"));
            put(-8195265773805934756L, read("mio/-8195265773805934756.bin"));
            put(-8198470913248626461L, read("mio/-8198470913248626461.bin"));
            put(-8381037589029380890L, read("mio/-8381037589029380890.bin"));
            put(1539974198544823836L, read("mio/1539974198544823836.bin"));
            put(1663163836379652005L, read("mio/1663163836379652005.bin"));
            put(1790238393984445022L, read("mio/1790238393984445022.bin"));
            put(1896199715147295766L, read("mio/1896199715147295766.bin"));
            put(2068539170343643742L, read("mio/2068539170343643742.bin"));
            put(2138326405829089144L, read("mio/2138326405829089144.bin"));
            put(217053583388222802L, read("mio/217053583388222802.bin"));
            put(312442932223993734L, read("mio/312442932223993734.bin"));
            put(340059997659842985L, read("mio/340059997659842985.bin"));
            put(3744080819900926215L, read("mio/3744080819900926215.bin"));
            put(4170052429374349922L, read("mio/4170052429374349922.bin"));
            put(450073944724737926L, read("mio/450073944724737926.bin"));
            put(4745936108167237997L, read("mio/4745936108167237997.bin"));
            put(5683599352333742209L, read("mio/5683599352333742209.bin"));
            put(5685221327652941277L, read("mio/5685221327652941277.bin"));
            put(6262805706691898965L, read("mio/6262805706691898965.bin"));
            put(6356538713265320868L, read("mio/6356538713265320868.bin"));
            put(6362189062087683333L, read("mio/6362189062087683333.bin"));
            put(6385281682528407296L, read("mio/6385281682528407296.bin"));
            put(6520430417310117223L, read("mio/6520430417310117223.bin"));
            put(6735045970717493054L, read("mio/6735045970717493054.bin"));
            put(6758778778841188374L, read("mio/6758778778841188374.bin"));
            put(719574448491822624L, read("mio/719574448491822624.bin"));
            put(7486200784412077737L, read("mio/7486200784412077737.bin"));
            put(7532973076254138070L, read("mio/7532973076254138070.bin"));
            put(7949560956520799428L, read("mio/7949560956520799428.bin"));
            put(8325382314658498605L, read("mio/8325382314658498605.bin"));
            put(8480236269238451330L, read("mio/8480236269238451330.bin"));
            put(8810857770749952228L, read("mio/8810857770749952228.bin"));
            put(8818174620704159780L, read("mio/8818174620704159780.bin"));
            put(9098684348202532870L, read("mio/9098684348202532870.bin"));
        }
    };

    private final ClassLoader loader;

    public MioClassLoader(final ClassLoader parent, final ClassLoader original) {
        super(parent);
        this.loader = original;
    }

    @Override
    public Class<?> loadClass(final String klass) throws ClassNotFoundException {
        return this.loader.loadClass(klass);
    }

    @Override
    public URL getResource(final String string) {
        return this.loader.getResource(string);
    }

    @Override
    public InputStream getResourceAsStream(final String string) {
        final long hash = Hash.hash(string.getBytes(StandardCharsets.UTF_8));

        if (DATA.containsKey(hash)) {
            return new ByteArrayInputStream(DATA.get(hash));
        }

        return this.loader.getResourceAsStream(string);
    }

    private static byte[] read(String path) {
        try (final InputStream is = MioClassLoader.class.getClassLoader().getResourceAsStream(path)) {
            return Objects.requireNonNull(is).readAllBytes();
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}