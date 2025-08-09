package nick;

import net.fabricmc.api.ModInitializer;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unused")
public final class Loader implements ModInitializer {
    private static final Map<Integer, byte[]> STRINGS = new HashMap<>() {
        {
            try (final DataInputStream dis = new DataInputStream(Objects.requireNonNull(Loader.class.getClassLoader().getResourceAsStream("mio/strings.bin")))) {
                while (dis.available() != 0) {
                    final int key = dis.readInt();
                    final byte[] data = dis.readNBytes(dis.readInt());
                    put(key, data);
                }
            } catch (Throwable _t) {
                _t.printStackTrace(System.err);
            }
        }
    };

    private static final Map<Long, Integer> INTEGERS = new HashMap<>() {
        {
            try (final DataInputStream dis = new DataInputStream(Objects.requireNonNull(Loader.class.getClassLoader().getResourceAsStream("mio/integers.bin")))) {
                while (dis.available() != 0) put(dis.readLong(), dis.readInt());
            } catch (Throwable _t) {
                _t.printStackTrace(System.err);
            }
        }
    };

    private static final List<String> MODULES = List.of(
            "me/mioclient/m$$p3UZFSGDAEkQYIQdZkoLX8zOAEPoQZ9eh2zTfIoID2wReKPEyzEVTn1PfoswmO7e7shBb3i6X4HREKQKmVg5NGO6WLCopiOGe",
            "me/mioclient/m$$Ukv0Bo29jyLAefxSUeHHYVxg6eQiVaQCRiS2MUgD38SNAL8tn6uxJ8ZusP4GYoKpHtFifbk3Pmi4yqW1PJDXPtZkF9qd6s7YT",
            "me/mioclient/m$$4Ujmf8hdke8XTxUNLnwfq1NRD9gAfIPfkVLwKbfEKSw6mUlCLGvxUV3zVU6KIvXIbNu6CjFpBSZ91fVSt0G0cDJlaOolsab4A",
            "me/mioclient/m$$HYVTylsFkqZrMwbU383BHiS6tIDECEEBj7z27nc8T8X5pjW3wNyOimcYSSeDbSThT5bJBJfYfm7bmWbeszO5Ino4rhWO6Mfak",
            "me/mioclient/m$$9xG3Mis1MU7OZtUbUikHl3I3NzpoSHEhTssiKIAn3y3KJf3M49CoiyEMArp5YnPIhGC4SlLN0KUruML9aSBqv4k3XP0oSXRMc",
            "me/mioclient/m$$Sa9EL6x1Z9Ql7bo36MeQFajuM94KyzDMfXUXVs4dWApHaRVWxAcRnYD1iRGv2o9XopoaJ0Ja6zJqvtysSRryk9tfO32Fqz1Cg",
            "me/mioclient/m$$CGDL7Mvqcd3EGG6xy2sO58KMrdn9bAQ8ARdNe897nitd5mHb4nyvP2Hr5d6RIZ5cfTEisXDLBx0POoX3ue2jxlLuP03yw2Gdg",
            "me/mioclient/m$$xQhInQr7unM5DvCMmuPmDYlV59KeN2i3zI6UTwEHrPvguP2HNlDUFl5Z0EHp9hYBinKhWhMcthEgNawUMhguoryJDSiEvZUWf",
            "me/mioclient/m$$51ElhQGUWcJQrwWAgBifUKD3E8l5AYrcRFZC8itakM8L8Qvij6s6azKcdnDcso4ezuvGfCDcPLwCZjM6g3qH6FR7eI1xwAH26",
            "me/mioclient/m$$VCFc7or4hzhCbIrUinHRUO1YvSSfJcqfKSFDhv5mAWrG8T01ivd8Xmorv32VbhvJoN2iILOnK4qtEaT4ck8ae37czNyvcBxM3",
            "me/mioclient/m$$Vur9adbRCqvxTgULAJyE3dNz3IAmfrfUNDtB2FxsFuY4cPYHLtTZcodf2xac7SMMEgud72NrjNrKUCB6VYasuDePTvG1wniwO",
            "me/mioclient/m$$T1mq4r2a9rIVEBHeIhfao7QSaKVvtIBfjpj78wJK8Xa1rAFX4HXOW5ZajbwclRZ4gzxliTCTUUmvQnUOq6yPIDDVoAqCi9qVa",
            "me/mioclient/m$$89S1MJ51hEPK7J7tqnd8pSWavaOL9DsByGgDQ0p6I2F8Wx2z4W2I1XRn0Yv5YJbIx1BEgaCyQtXcF9kKluUPQb8p9ixNCngBL",
            "me/mioclient/m$$oMoOMgRAwiQ4BbVrGsyafEGL4NJepWrN28oajQW6MO0GQymBb15lm6Rdxlp3ncVyg91C1X03F6tgpnwsaxZEWyyTWEXb8xSlS",
            "me/mioclient/m$$TZgQYOfileYzvjsLQ9Fdn0ZKXPPt43hknZD3yHKHsxHe8L6WDDVm4dArbek1an6T0zOqDEhIQb67DoIQhI3hLliFn0go1enFb",
            "me/mioclient/m$$aCeIXoQEMAvdcylIe6FkK0mnr7wVNtX1DL2F1iZAAkKjICMSs4KAm6s2wMMwuER8JPDgAYbp00JQoSLHEQ0rgf15HROMRax9f",
            "me/mioclient/m$$8QQTpK1RxHzr5hmXD2zJX4pJhsnn0e7HX2vgVl4GE5HgFzSOo3AhfhfqKOQYEB94FkI4Vjk4kL4twOuDP6B7hCkWWPQioFyET",
            "me/mioclient/m$$6YtGSCnL6yFCmH9E2J2PQ6nRIh3TcWcKBrvRKP7hQWhPufaCIvOACnioXeKW4MwNbxprfyl6CloypMSWUqhOZVmUQs2PsbauM",
            "me/mioclient/m$$F1XdAZcvTe42YnXOGORqVM8j1DJhgttJm6DD2b1vPhSiYLQrRCEOsRicVM8x5rm8fl4lEwtGJI16YEMlzETZOXcjKewSZ6luv",
            "me/mioclient/m$$o6FqGXa9a9yB2cjFatb5uJLCn5qqeB6PcoCZAPBNaNjH8E3Qd7oU4BxzMJx4zNhaSB9RqWp4vpUnFSDumhVmVWjG50avkbgbj",
            "me/mioclient/m$$QnKWYOYEBqeR1qzRwMrDwDyufkTepUvW9RfeA7TWPCLwRPqHe5avbHK9Nm9BkkllvHLjRCt76X8GvNpqq3zwXngz2xseRjOju",
            "me/mioclient/m$$55FXdxku5WkYQ4RddAxVg2QrIdQtzkkMkeb37cAWI0KmqJKsTseL5hWf1RoSI1cZQSTVSKQUTHGa5nkpj30HutoiCfzOpRiA4",
            "me/mioclient/m$$7dBQtMll6jPXZNNL0JKdy4p8eVbJZI7oOTeXRzLBqSEEEcap1JnngIGWGk1FEqPXsknugaL3HnAmbnnwAQwHFkcn4wsLUZFKH",
            "me/mioclient/m$$8ur0H8Bbo0FsxTE2LD5h71rqcBTuUXgP27jDaTWBkBfl0wPzKTdEPHYh5sPKj9epx9ZpsUNYlMGyfCG8JdYA09PfoSCZeC6YE",
            "me/mioclient/m$$WIMUmuqPxV7adU86wZwBfu2dtr974BJtKVmN2SdXQR1QoUf6tDQQhosobH47teOOLiezGrzQetS3WL5oBFaPFYctLKn3dK3OX",
            "me/mioclient/m$$nnkJCUeq1ARRau0JFT189l6Znl5YOvr8iibpxTX8R0HFH7iRoyoEKVyUEGa2iiu7F1lhIubZ6AZ3FMY5ubVoqfxJnAIWulSve",
            "me/mioclient/m$$IoqZd5c8lhRKRmrIyLwzACAybjxgQ4rXUTtQ5Nnp2QD5Pt5yIADlg4U3GwlDECBMYm0tqc6gnWGTAsZwQyTElk1mYEUMTL8FV",
            "me/mioclient/m$$0siHm2lw0KAKXOmoQwnZSlrHgLKIzz4z6Y8HuDDaBsdmUH2GR0E5dte3BNLISSGVNQjO1oXgOAEFApro90tiMRBadJlnRfyvn",
            "me/mioclient/m$$pOP9ZR6d4KfRZEbsffwEu35bFRIMEqHfTWZmmqHpl5JEvcv1Ctd6LcrhpBCKOBRQANgCU1t0tM6Z2qNsRAqu52Yea6kHm2GnO",
            "me/mioclient/m$$EKSi9psJlTKMlduA3xZo3c6lzya6ErEuJjgAx9z9dtAFFymzOQob0NX4IGOxj81pLYXhXhwa0jpySHbMXl9nXj0cpbG10xSR7",
            "me/mioclient/m$$QNmhDM27eGY3w8yYdwou28pZAnxCe9LFLNaHTHzwlcF31i6PiUkv81WirfqrEuGVY4yJyJ7NbYNaqzecc1yCQ6HqRujo02ujU",
            "me/mioclient/m$$mX1FqMWwTWRwklTGgChscPRawoeRFgadk86TvHELxA58u2dGUj6boG8Ug1eafb82lU18gk3oR4aH8eKbDaTlVL2IGaWLPaonI",
            "me/mioclient/m$$WMvpZKq7hW1sKUdFJlWCVULVWV0oumDpbjJ5TvkzuopcPa7cKwLoGrgQ7uPLRnl4VHe3Hc7WhTiV3NCRPwKKbuWl62ng7lvtU",
            "me/mioclient/m$$8w0OWy4xpsk9yfgapnKsw1nVYB1vvTSAOxHXmrqfqqxY9JQiWseRSNgfYIL7Kx2pX5Bkb56KeSVthL48epcywG9TiftJx8TSZ",
            "me/mioclient/m$$0mCWVtpihU5GSPj5gylZY14ZMz9vniNg1E7vWfFJ5niTTp8p8jrrtg4M3Z8CqPRXCBjfshVRalMpyNfktZLRTKIkSrnMSuHFh",
            "me/mioclient/m$$XNjI4qTMOGw7jbW2N5fRxDQEtQR1shDnrvowi5A5jk4fWW555ygWHEonPF4Yv4AEwgwr6XAAwEX7CSi2zcv3K88lccpZ8HqIS",
            "me/mioclient/m$$BE9WpOFutcj1hqs2LtpKg2tCrKSgOHTNYFYJkC06oIjWQomeR7WfHAy2CHY8ZFnRrDFQKItCkzZTMYRaLenJkGV0DcnbBJeAG",
            "me/mioclient/m$$1a8TOY0wPcIsr5H3HtBQ2WvrIQ2rUPCAI6O8qIdOJihaZYw5Qf0bEzMEhbWsHio4ZYsjaJbUbgdwONbQqne5qR6W0fVb8bOwY",
            "me/mioclient/m$$zqZ6ERKL8aNGtzuf4dz8cXvw1rv61SI8tVP4RFCtUjd1oOwgjk4FRksEeuka79ESNZP2gjSkdVXIgTFoS1Tpsem3IMKZ4Ul4q",
            "me/mioclient/m$$U921rxwq91wZmhfXZ9qLhs4nHlefVeqM4NFUxT5KDxR1mVnlFhPlcS2gEXQVcBSF1CtXXktN5kQyOfCniZDQ0juPHkLARlH1S",
            "me/mioclient/m$$wv2uYHyMuyDBLFYTCsE1ghBQxY956tk6f61PdX1ZLnNKhE8prH7MvjX0gL0HzcP9DWs86ViGrTUPhVqFmXADPJhZEuB885W6X",
            "me/mioclient/m$$BwEiPcdKHeam8Th0opyulrerFft2CdWz9fdwcP2Qplk3IYuKZt2JqwHabX9voGHQZ7psnWWdR9JO4Jg1hAXQx3x8cDnLYPk4u",
            "me/mioclient/m$$qSKPLudNE0CMQXAKDlros2m4T9mWvOVMWZK9EXLZBb5woWSYuajlpo8HgQxO3B2DIJkGR3cEo9ne8PMQn9mTM9sdU01cwp4to",
            "me/mioclient/m$$mpNGanWJ9i3lhk6RgIhdrTimSQSyPOBceofjpuDHEUbrchXJUtS8h9CKSPpXv2NrJU2RZiVsTcddaU3Zqy8KAk8l4jh9T7PKd",
            "me/mioclient/m$$cxFA9y8kYOcn2kbnV31dkeZho9LiFPfMwowhKadjEIzOIcpI9tz7PhFrGjv36dwpAH6nn51pOxOTnvbHETqbqFmsObFrJjbiA",
            "me/mioclient/m$$IfFacYpBdqOS4djmddqunVcOknusXgANMpma852R1wtxsiurQCP9WJEFKdnFVsZR8NzcmSfWPeXkBJ4NUWmv31r8mUW9cPSL8",
            "me/mioclient/m$$SD0ZrVmgpYg1pz64XxlqGKXUvZesw20OEJY6C97mTL3aoES2tGYiOESH9gHSlyYqqOQxpNW9lW0qHklfvRy83gnOueDHPpOEk",
            "me/mioclient/m$$Ynr1vFJawXYMkUv0PPNZoYAd10uLHoV0KI6XuNwFMfo5S8p1WNkG38mbJGZfc7qyD833g3OPhvXPdOYhqYE89KlU7HSX1pRY9",
            "me/mioclient/m$$UxJSYY5nxhL04kNxsSsa7MO9HK9FpEbZebeYNT0AkSkcYRebHa0RqXyvW23w5dxz5ydas7sbXnYXOePxdZe59GsR2OmDtBfyk",
            "me/mioclient/m$$Z2xlmFpV1rSYTAzWAzSWgDbEhwYPW8ruxPedz01ygJvw0VohNLZvIquxQYth1vCvgKBP8boKpHFRKQQriINsSSFvAAV5xozKt",
            "me/mioclient/m$$5BcgLRmNmrUBECXEwTiy6cofamzomUCL4sqybkhddSjt6gSmLvujWkx3yFgixNPsQ7MCaRzwCioutKeVPx40kiXsAOE0nOG5j",
            "me/mioclient/m$$Fap3PaDoEm27T3hPF8Dsa1AgbjcZpzAYytx7EhbLuM8fVvitgLXfWRlmMcfhkLGb6X2sF5xlvvGMmskjXSeF4gSaPAqMayMos",
            "me/mioclient/m$$b10aPwHp9WHltcrHcF9KeUpQiY2RIaAEJx98BQLFInQC3MXSSPgbtX9VKJ8KeRrOgX9sW3clXvaFp4Ndsy2BVKkCRkxkVwgsX",
            "me/mioclient/m$$gS9PAlaXAxL5QeubWHIAXb2SMQHx4ZqJccJ6xSaLtSYmzF3BbRnZ7lTAqYuOwjpd1rr0iHdIrcGWYv7LPrkcBv6pS9NWy5sQB",
            "me/mioclient/m$$MlVgzB2JVRjq6QMKU3CuVCPDeX0X9LYqBF2JclpTHxCLz9o4GV9wCCJocybi1gkRc7TFRglDBtkkrICsafCJ6yCrQ6Qp0VYs0",
            "me/mioclient/m$$U36dgriWDr0B1UBvofXodgUyPeBydXA8ZUkpLAKHV7ElDfNmXh3QX8hgciIU8FoLj5eGerDNkwyIvRWwSaqcRTEYx4poBkvwK",
            "me/mioclient/m$$FnM901iotyJ8np5OZHJVIeWgerQzTx4cen7MoBqv14rnLzOxeKKKjyIxTwvqoQogEuOoO6EDnpVS8XdJljF3dEl073OXGxQuk",
            "me/mioclient/m$$GRfkiQOGdhc6l7JU4eYkQXxnRmBksLgA571WX6khYEh6fOvU485230Ctq73q1ObupqxSr0m79jkYgL1WaY84e7GjSi2lhnH5M",
            "me/mioclient/m$$rmT4Jf4temqhUS39vIBxpCX0tZUTeabFxV2LM0jmeZZcXe0nxVC5LMwW6U8kZGvnH1WrMdXmzgzLNelPw7ZmzTk0TjqBOfDBf",
            "me/mioclient/m$$nSEZPg5ooQWw4v02JHtxvugLeGJPbJBll2Ekt9OPGf3NEngYPBzKnmhoM0VckBIgDUoC0q3IfQWoX3qo9iy708uWaT6lXQrxW",
            "me/mioclient/m$$O0cNpCgSMBIljr5He4O8g5FFmMvg6lyYs8dc6SwEVCjZs7V42CKeBMeOx9K3YgTeG8ACZYFGWXufIpWvS1dMcVbKYgwnZXSnW",
            "me/mioclient/m$$DNu4i7fgBiloaRBFv7j2VlGPGkXxcjDRQ2dO3h2XGwMUvG27tJQkoZZNbX7VeNQNnIvDh7d1VPlWPbJpVBHGE1C9wTY1xzfzu",
            "me/mioclient/m$$DzhTXoGvGINdr3Attaw73GObTwFgl1s1fSOLnJC4EC0R6CHdcWVT9mSgKFGub2M7tcJBvP1ewW9czdheZeHadJAjButcX7KKk",
            "me/mioclient/m$$qOUaD8d8X61eUGi4L8mrX7ycIZXQX4DRp4rxTIH4LmEWvG2FLoU5SMhtDQB07a6rwkFFbkj9KgXzIQYMRjojrbeuRrUvJYT7X",
            "me/mioclient/m$$CCz0p1NGU7IhTmsbGzF1vwT7ROgn9ihdf4WOqXq6osuHBJKSQ7PvJ3STUTMzwfvGJuOCOwBdfQrI40SrFBCQMiDEZkDOEiZ8b",
            "me/mioclient/m$$cFy0S2bS7U2m7bzc3O5IB0USk2GXplPOWMwkPbtlcMqUPwZdqWrImGD45wOTIHxHoiuNbH5qx8JEe1cGK4OtWr7Feso78ZhRo",
            "me/mioclient/m$$JWkE1vFfNBqsaajSliM41PCH12JzaVx7aOFnkJ8MGcJusUH5vklvWPh36kE7OpUjZt6VAgAWcOqtHPG9t22Y4WDHmf4U3vtyX",
            "me/mioclient/m$$IJfsvxfA1qvVmNjcnZfx3VE1YocRWwcIMwYrBheLWtDm8s75SArFqW6l58NSYimfI4QUjNbnWWNwANX8QgJZ1C44tG4DXX8Wo",
            "me/mioclient/m$$JGENUg0KubOA38px6ymsQVAtU3vnLV8yxHd7RyD0dGDA3KbRLAj2czUi9MZys5lpgekRhGTgS4OYUAHZGOplJe11pQgcDwWaP",
            "me/mioclient/m$$N8QtWlvqAjF9b7bkNJpqPjgkZnrIeWCho0lqPqhgYZb8rEQsVzK13JA6s2fFjtEETYNENiRvklcfrING5GC1AvX4sMNT5zE0k",
            "me/mioclient/m$$RVXdTPEBtwYgCgSacl3VK6lBAUyygB1olF72V6KfrF3G8aFX6KxoRVFhQIsgPbfpQfwCwmxBF9XUM4yTtoMPBC0d6RvccDjm1",
            "me/mioclient/m$$0opIdXhseHDRoWcUgHfCU4bUtWHas3JXPwQnNdNbHzVToRH1JBFH8evqhOS4vORnYN0NFrKPFqe6Zy2C0FF4dCVxpwT3aUAZB",
            "me/mioclient/m$$iJ7uNmrRD5QI50YOWLrbGAcPU0swK0ZZrfUZqQfygnbBzDV3wpAZLikgIWLyGLv0xgcVyjoFDI9C5BHbn8JCpYpxqVqofRs7J",
            "me/mioclient/m$$XjCya3XeVSSx0LOHAziORb9O5rrJM8OfIkJITZFrpJ68LUPzKvXvFK0WrGwtk2VY9ijkqTUZHzxSZjGH0FDmoQfb7wIjQP2fK",
            "me/mioclient/m$$e16XvkpNf6Fs3Nt21SiZ6ZZXlNwBJkhswnIcMGZQn67hRisqZrXNSSFBYqIeBVwEBCyJVaYAYkDEZHlB9ADbzbPyjLgjaRMTy",
            "me/mioclient/m$$d1ZcMbOmyOPoTG1DKXyjGX3najjvZU9E5ZjP9menZV63VjKMtcA6dzwKr0HFfyMndDcALPEsRO5rAC1s8VXOzvU2M4rJtpZyl",
            "me/mioclient/m$$cIZ2QRLMfPhWL8xVV5Rtbm1aSo0yWp6il50Bu13CmyzSBn7zVsGUPgESkLsJRn4Z3iEQa50HhRW7r0NhVn5KmJb7gzv6cTKsE",
            "me/mioclient/m$$mMIURtTiQBAyACMCz1KYSZQn5qdWXvkiZpNaT7PkXpePDnaZlPJrVpBplMlL8XfCD1Rok9bv0XFDV5ZZWXR1LtqzdiOPBm7D9",
            "me/mioclient/m$$2w3aOM2lNgFiBoeeXgyywge3Mpq767xIj5hTIDf59iIsRiffwj6RmBPWU8f5JkWDvO8TukubnliLyHnyVH3KT3yacgN1udarM",
            "me/mioclient/m$$hbpSAO8YrfjFQ3hDvGB1czBsMba2J3FvLoj8OY4AaBeawpWo3snlTKPnByukxuzxXBg724dNMUbwrf22JNTV82RHUm3pFOjtl",
            "me/mioclient/m$$4bDgNKPOSk2ptyK10jT3XHuqi1OdHr37L20nv7xqLdp4prc8L1rXI0f5V5g4FaqwIzGewHK9H50GrFZ6924hXzbdF1TZDxElK",
            "me/mioclient/m$$zT1UgsJzzIatZSESgG7E7Yo3y3xWh8PI4OEM7rvhiTcMAeI7EEQfffo9ZTFd6Zn8RxMOGLpXiKQtxerKQ8JIl9n6AwvWk2FCn",
            "me/mioclient/m$$JTxl3Q8NAYnTvNRPLuKlTrZfUCEDM5Cr9I3zgBplQQTFlsGUckCLnTJqM27WFpHpY8QgWgqM8p1UVg1zhJkRAfFfsSTCfM6cO",
            "me/mioclient/m$$sQwB8Gn4SsL46dO2Upi4yBLYPCnKWOlOaSGrxKbwA1RWaIibNyrn6OSioUC20gJrJQ0HrgfZY1nKYe7wPwvGZ4a6oZ3eJBH69",
            "me/mioclient/m$$jcKf9e8ZKbRbtG4fejguzjnQPOLE2P0AhgAiwwxdhNs0kuZVO814jJ8GSn4gU8EUh88Og81Fl1HzogjfgmmEK4tTKaJnQK9Qb",
            "me/mioclient/m$$2sarOl475rTOnOtQ2mmZ96q7JkloyxBRwa1vQ2ShOiBKEwJoZZcMhyPDF8V87MaYPnE3WSoEbmBNISKaf3DdODsSMBkvBEFuY",
            "me/mioclient/m$$p7or7ve63IzigjaIeyIbV2wd2FmdINQ2lqvF8qbPMscZy5tiuOemC8TuXgWb387fk0u01mTMLYTinXDvEI2mBj9IXtYvB1qfe",
            "me/mioclient/m$$qgZB5K1j6blsUuRtdbxdqA2hGPVtOHC7uALHjb3lwKKdMtReYrYBqJt4L75jH7aWxDnSSRucREqdiOrazHMxVk5dSiGrMBHFF",
            "me/mioclient/m$$ZvbGDU4FN1YoSkxBe0OdwnjmnR9qyYvRdtMLelcnSCZB3VLlrO8ECzTvegv29XaCBhfmoT9Q55ldP8akM3c21IQJaWxlmjLao",
            "me/mioclient/m$$qwqWzyCszuRg2E95uXVMRo5uOT52nW9izE922Hv3zqQJ8rd9MT28W4Os2lAog1zlGfmrhT8VOP3DrzSMAnwIBylr5iOdsLbLc",
            "me/mioclient/m$$l8eust5OcvMfpcih8qOxNw6Eysrk560MuM2YP8ywtOrJgc2C04xDCwZks5q4A1aHdOjJNDuetsRhG3KTtdu8RPorqi4ZuEsDF",
            "me/mioclient/m$$rbBYh1zLbIqdtCBaQSLaCb7i2FWgGuGykBiJAgNQxajd0XOaas5tNQPhObIUqfW7HyJYhh5b7b8LjDEciauc8oS83V5CDFijV",
            "me/mioclient/m$$PoCvyj2Qv1Lp152BTHAo8MtgaM3QvP6TbVz0AlppfrP9iFEAkkmD7umnkhyhYuAm2PVh1cLRUyjtmrybSKVyxapipEW1PEWYk",
            "me/mioclient/m$$q069Trf5E8N2wDsFPNSddMMbK4AJ9i1mOjqaL54Dkev1WciXlyK2M0Q4GYPrmAysHYbrphQR93BWZvjX6hztrFVanXnnsaCXw",
            "me/mioclient/m$$cUw1Gfu22JqlKrPTm5plYd8EyeqPxNQEATXPCSKvsPLOywkjL74BIoX0Ams6t5jZnPsyvVWJlntjrCuaqzk0MxiCz6htRz4jb",
            "me/mioclient/m$$7yTgVPcMmMB455xYb9v6s80DD7gXT65MpkWzR8WkNjfcdRLFgxKNSrcvqoeMwxXJHOqaSfdZO45kDuCvFQUSXTqXsiaTxXSBH",
            "me/mioclient/m$$kirjeEFqaNAODWLU5EbEBTMLoYW1IYLx6NUGxwfkCn0OXtUuT6unPzPbvPTLcsIPcffdGosSYtMQZreq7AefH7NsNLvgaJeYX",
            "me/mioclient/m$$lQ217AfKWaAhR0KRkEt7xfbDyZKa5Nejm95MFzSUs2HZhJUCsyGyloaXDhXBU4O44imbY6pHmmhSj9gbAkeHrHESbinreqKZD",
            "me/mioclient/m$$QKLItOxhSKFN1fKKF0jqlSIiPHI5Y8z9XJRGeLHkK8Qd4WF4a08Xa1sxAys4sZyD2j8RLgbAaNtkwTJ80KpAxqnzJSbnCAgar",
            "me/mioclient/m$$Wb8hOEYql7hFXfKif2c28sSoflnOPhbHKQrr4g4eFuNaz8uYQ1U9k4I6btRoGptwM6jK6DxR8cNuVKuViEQv1O2tTtyUDhn8N",
            "me/mioclient/m$$vmsK0ZMm7FMCXgM9rp3AZZmVfWGAXd5Kdi9AD1s2rbUFLxCYhxUqG2PIGb7jAdzHjj3RroXag2fEbexQpH6aZWU3jF9w6xBZr",
            "me/mioclient/m$$wtn7JxsjS7H84JBFGfGgZ6oMegeFJZSHxoMCEfaZeiv3EjxaTHTEuIs2FJ0fM9t698Pz42a5yPln3q8V92JY7sEdP8sI9WPml",
            "me/mioclient/m$$QHoubbS4V8hHj7hWjOASof7dGrOfMKeUMphGGrxTvCc8If8awgLffQClpSyZoIIfh6VAciaAiSq1yBpGP7vlWkMLr3v2Zwoi1",
            "me/mioclient/m$$CGZxfwq3gFUii3sdqvXqiKr6IkFBZNiPMMvfON32c33GbUqCgYOECKO01LPZ8O4J9hlaCnNQMJj71IQKHyyD0SSmRXxDu8pkx",
            "me/mioclient/m$$tYPCqVTEwMHSL5qKxLiYhldokGzlMNeiNGBEB1zIT52A6PRFVcAOnt31hzq5jJFpcXnfmstTgVcRfzXy3WjSOcGi4CsN2w8sJ",
            "me/mioclient/m$$42OWXSR5Yih6hneQ878uLJwwojSiZ4CkjtZQZgyVRHzCiXa9sCwQP7dciFOSRUPr1v3nLTEj5BTAKjvt5Wc67HcEnCeW9ZnkM",
            "me/mioclient/m$$OD9zgN5oz1bJ6JdA8VRz3mhjYAfvFbqBk6n8hWCpjb8e1uHPsaCpPcIQWnQjFyeLaPXlW87BkivzeKz64qhoLTn8WGX7SyJKS",
            "me/mioclient/m$$EOkWgunNz8tWK3LqzrtqZnFgHsYI74sT2yT9YKDieCxFbpkmaNEEcj3HKvCV8NBfwKD0Iu2MR6ZqHWWpV4rFvULnCpa7OEhB6",
            "me/mioclient/m$$RJ0W9Qn3knBeatanyuXiiBRe8ztdqOxVFfanP815xW8YlImymCDDKWUWLm6Ges3LCy90qvPWUPoCbmhwau42E6Di7aztbwzAB",
            "me/mioclient/m$$WAcod9oqqNq1P7J2vcVrBo52RCesUfsHAiPgbevn8uxuMqVCLzQj6RL4zGbVZcYrgU1oWVCalLAylF2lg9ByG3NskhNrS1Qni",
            "me/mioclient/m$$cLoz4UwY1GSOQzXYErRtEVYBhrEoLPlj4BbRpZV1TLY7qk3wQNZabEq1LQPwG2CSYkgd7XrSLVNpuykeq21bMvSkKk4Fe5RrY",
            "me/mioclient/m$$mY8K6XkAwHrfnzKJRfQNi6RugEBfTLS7mgUAw8i6LTrv3YNuKZQdP4Th4RlJhw29Wn8UKA4t5QtcANLkd1648zjUQMqprM2f4",
            "me/mioclient/m$$nBN1zcf4EDOJV5Rky9MJuGc7nFuXRd7lUaVnoM7MKbfN9Eoa53fRZLKaRreSCnfM7EkXfSAmha7zObBVITEm9CTQnF5r99VQc",
            "me/mioclient/m$$I0KWdaca8oT9GEVjgh3sxEPxtmT6ZRjPVL1CqqGYd5TBBjQ1T7O9AbNs0sySzUBCsOJdjggDRoKZ2U7i5mHhfNAJxbYXmZi9u",
            "me/mioclient/m$$m5Dix4XlWuR80x1BSbQAX1J58UDglFJ7IQtxnM8eKeJZeqcP0jEay5kmVh1w06IxS6tOZdry8Tm2IkGuA7FQsMACWOJ16wzrM",
            "me/mioclient/m$$L6oCxohTw3BvQ5RgtUJE4xR3AMMyNFK4rUsWUp8GROKI7f5fWToF1rrT0JkcwuZ867kZLUt8KUgh0w33g8oOS17bLchYziWTG",
            "me/mioclient/m$$JVfbsr0i2kUwdZuW2ppHtw5kYxkYaw8Buq1q8e0wYak7bU2uYFnc0acKUFvTQkbb4hmMwBgkRAcT4IZ3uwKNbpDbsAngeg1Qb",
            "me/mioclient/m$$3A6ZgZ289Su2Z8nxplERHFHgeUPigeMo8PCoOSnPSiUMVIC7a8luEGiV817HWaOqkAMrMTifKcL0zLcQgJuEFTiXeCHdMMMcl",
            "me/mioclient/m$$x06Q8HEQswVSiKf27YJjNDBB1NrpDWcqUTOP29vkDqJJHIy7RX4JnMmz0BpZSwwxAWTAntB7DuFmWGIIBfaFGM5L6AWYKsw4k",
            "me/mioclient/m$$mznqbEfJg4kU6jBkq3ItaRJBKz4vkyD0lI76qZ15U1h3IUNN8oJxfzaw8BIm8DD5YH5HThgIvzc4x6U7j85UlHKCkabRrI8qI",
            "me/mioclient/m$$B82VS5c0HDa1JwZHgcoQ6bbGidTSqnxLDs6iswiDZNzUZ8VrICfkbSaezRw3voN9FXKPimMuO9C2uBefXwlM1ADXnvrexND4i",
            "me/mioclient/m$$fYuve1ZpdYRLms3Zv2pREKwxS9Te7XMU75z6fLP5mR9yqDQXEHHOw4yUjceAucPtmGARXKRqaXI9JJ9VyODer7dRPtLYKLWCo",
            "me/mioclient/m$$4plkXH9wxWJDEtobnQ2FdD6yWd7882h4InqwOcVZnQhkg9O0YoQmLJZnwQsREe67a5G1WT9yLl50lY3uJiD5Uh07N5GkbgX3x",
            "me/mioclient/m$$RniRWXtiAdcmHO4jrec9gSarj6wyTj5Bwm29YZKcItrovIEz2vuqLuswtM62XBOfGpz7J9Je6C21IoEgaK2wxnq7BDMwVLmuo",
            "me/mioclient/m$$zPP5X2O43bfShCisUmmK8WVN5BCtZdj5Y0pmQitJg0dES6AeNHEvtxNowkURPcUXIzxsO9Z9TwZGluxZ8EiXY9w3Pxbn8NYjj",
            "me/mioclient/m$$3ROV0pLHy8Qutkik2SFQvkGVM8euivjkkYIYEmoAg8XdqHDckodimCpht9rNE2LuWaMUjevPpCtFnBhQOM8gxWy52zNVOCxW1",
            "me/mioclient/m$$9sagvGzS496WQhMhMDSUAa9eoNeDy8S3nPbpVkXCySLDdYd0pIFxZ4DQrTna78KUP1UVTJqdNLF5ruKUomDLZdSmENKnYY915",
            "me/mioclient/m$$983GknrrUVJhUG6ZmzpxthAT0ER0Y1mC0YHDmF04yF3lnvoWpydofQhOZxEWg7u8G5OuDoC0v1AiDTIwEh6XLIsAvr2gqRg78",
            "me/mioclient/m$$0ELvbOGPBPwaXaKT77mQlfFqgSatbg7fM0zpWHyq5c3v7FsjbOz4FVV9ktWElsESGM7r1stawHXW0J1HsxG1A9J2dEC9RkIMb",
            "me/mioclient/m$$XScT9UJ7tCbChr8TmLcfKGhqF3IQSAENrTVDORRzEqJyqfMFjooEEjd0hgb92uyJhBcqNdgDwPLF3U8IkynWcGUApM1bRqE8Z",
            "me/mioclient/m$$iiNZLVFblS6sm1rv6sMjDGX0B2OcZFaQihysISxXZkkKu8mQU5Rc0pRlWah6czHDKAy5gLG2K1xXLnztFcTdSOTXuXuW0Z99U",
            "me/mioclient/m$$mLAkRmscdf3d0QVIY25jllCt0TRMc9LZRM7StkWKq0Oq9b6UYYH2I2jbQ6t4GAMP1DUjRq8tilQJamyz9hx3WSwqHEZOB5cPC",
            "me/mioclient/m$$mcLaPPN1sSjTsTSi96gpGOrXMZYMPn8PwYSDkQ6llyqjJ2Y2Q505bzfff3UL9kT7mxIv9G0tGySltd9ZTpYVarJQProNgjM2k",
            "me/mioclient/m$$FlZePLmkTbZJIFnkGgmCjJ6pRHZQWOkPDLAe4mXCHd5amsmAyK7d7X2LlfsWkxxSYUDstcICvnHycRHBOCMwLGg7VynehID4T",
            "me/mioclient/m$$U06wAdbal9c2tydaZHi7748TVcVwW2bZgovjk7GFooaETKTiOhGg9yB0lZ1Y8aexU4VR3M8NH2waRfLg74cum1hWwGVHrgrQm",
            "me/mioclient/m$$D0AxyRrSdvXE9Lz6WUnYi3qHf3WXaHfczaZNhxwVL0MJJH3uXzXIOfBBqfSiDPAeFQlY1516ezwu7KY7ZhQWkhPMCbcADM6t9",
            "me/mioclient/m$$XmHbvm7SEd11AalaLva6Zkl7ND4f1a0yu5RgoibWo2b6tEwZxoZqo3Ueiw6dDgLhclu0I7fS2PglFjD1TvLgOfQ94zEDxqO7W",
            "me/mioclient/m$$dVaCAn82rB3D00tSKVZEunz1mD8wSepk9UpZTQ1MdKy5ayRkEKsX7vX2kSMyjfkzeU4s9v3Q7PyK7K9vkWsKKmfq1G8TGFz5o",
            "me/mioclient/m$$FoAdowZ5zokiAfqIy4MYaxP17Hbv0BuhFAMy1LU65pbsv2vK3qQ5Z1NPrDDeDGfD7XBPL5zf378MRcZ6BYX507E6vAWbR7Ac1",
            "me/mioclient/m$$7asR1m6msy4qQ8giUQRI0c3QXEtnOWwZKmmsr7wSAfbWgaqWaCsxFxVeK9qeZV7eQNtgorblQKOvkRA51a5EgMlPYM8jR8qJS",
            "me/mioclient/m$$uVIFkBgj8JoqhMxROywPx1r6LVZQKRc19yxBxNEqHA3Ps2EGBaj5ZlnsgvmZkKvPCpkSf7XRCRxqq4elhgeVa6Stb431yyTUN",
            "me/mioclient/m$$MQ07SPB4WHHod0p9H6ZeAcRvlcVQB9t8W5nODgWyl3bdM07lM8ORYbKsSx7yuzQbnRUd8MGugNki8U26dZzsAvcnsWNZFMzlr",
            "me/mioclient/m$$s2Xa7QsaWERckIBTsJnZWn0rJhxmT5XSjzxLA0LPQNcUlMpwuk9VWACh7pPazGpkXG8RYu8AumaVjEf4tyFNNKiXMqeqpZyLO",
            "me/mioclient/m$$h99s7ELCgqePayhRahLasaYun24wKUKDW2gZlArM3JeCVOz35uXCJIC7CcBgSnpDJ6l6KQBu16fWWWCpiXJmCdjXk04kzosH1",
            "me/mioclient/m$$IXFymR660C8AvtCoKBitsSwiW5Q9Z3d8NiSRPrGJF6Lqr8tEMYkpk7aN1bfupMncAaFE1DKrOYlpH1TX32mXyERLpnttjTbgI",
            "me/mioclient/m$$IpKDgL50X5zra0qdTzpiJpf7b5QBM9ae6HD1Laz0qa63VploDYwdMWESRsxVmw5j5sVnZqW1Vfi8Zu2VWjSJLwPBzeU4833kF",
            "me/mioclient/m$$PGfuxO1gQrJ9vIexZN0HoCxJtCvcGBIw6XLDNnxC34flJBti7elvfX0HHkXceFyhNmTRXbTNDC0ZmofkKxV68cX2OTQnrmdqQ",
            "me/mioclient/m$$Hb8W0QC0jj374slKIodUJQ6yEnAt9JWT9GO08CKGaUSmHWCrB9Kmzw4kkoFUWxxiRqrSJaCmTNX9JL7WHASiS2s754WYQebox",
            "me/mioclient/m$$Sn4Ga5XACNw5VzL1e4JWIy9MxyFMOEBK99caJvh1Ec5EviWHSsNypcVGpTCxkGpKECwS2S031FViU5FlRlQN2tNZ2pLxo0FiK",
            "me/mioclient/m$$Jq4VaepZnFmEW0Wd27xBLp5R1WsLFj5OUKdOBpeCkJTC888xDPzT8BbBgw0WBEPEeqFa4pPjnSN4jHhPnQEde79scBOviU0zJ",
            "me/mioclient/m$$dP1slNfusXHTxRvMAbonDTBY9GM7r4yLBzphW4o8zPfYQ6v0ly78NFayfjFJnofY2XGds7TbGxdQl3uijFET6iyuhwdec8T0V",
            "me/mioclient/m$$8yf8hmWcwnV115J7w4KrjVhYE8HEuE4PB5pBB8AhjESSuPrGzBSt6CuiF6rDxr5CjNTMNCqTQSyatgOBJq2g3NS6lC9onnNXL",
            "me/mioclient/m$$yD7tQUl5BUd4tpXnxjWnj9Ta5OY8qkNBwVipGzMvFxG0remUMOyOnvYvzceFw5VWKByu0Ez9o0mkcmDS7b1KubjfoMlQtPJPa",
            "me/mioclient/m$$PKM4JSpzZZ7gRMcRwfIb7klkhxV76tusm1ouFL1aDCS9KK9cGUgWzd8g8RxxngsuHB1n9xp4EPbUpyDriYb2UBLZSUa6qC1Gn",
            "me/mioclient/m$$y9DltqYTcck5lHmxoXbH6bZh1JIBsV9cGoeGckOFkblOtul0srYNJ2qGI2uQmAOcF6qXajZkPWaDPr2zjA26VdpC8BB5k3mju",
            "me/mioclient/m$$aYkWP2yh6iYXz7s6hExAsjzsMiK8cPoKCPwrBSMGxnJmfxqEw8eX8qMRJZ9AgH22X0l2BA7ddMjWoKwXuJzWbAhS8GNANqe6I",
            "me/mioclient/m$$SV5T3pIdM43gMvzRqCLMcuFte34tuNSqo1W1r2HlNrnG2NmJeIQ4qLH75sMyo9ksKu3RJKJ9ggN0SRKJpsi6mnQFeuotYWMGc",
            "me/mioclient/m$$ZeEUXHK66sCSvCQMVk2oHAkYZqLdByGCipi2GY5SBb4SYtqq8nK4e0k2eLAMgas9055SZunXPK4WwySQNoqEBlK8na4IAbpv2",
            "me/mioclient/m$$I6t76d6wLqSd6s7315v88EQU7m5XkRn1a6HGvkuWVAOrqlXugw6coKLqVLTq2Qyk0uPlrP87s5auxaazC6akZkGLxqLRkZy50",
            "me/mioclient/m$$IAlbVzg8FoI9lnXAe0kiBUgAIRRrsV0c5LXaNtIUNHEwNfCy6dcZP5YPnKyuuuVEBwTV6hTauVunSHQhWKvnglrRoaYhvBkK6",
            "me/mioclient/m$$QgS4ytB491MfSIAJRu84lYlaGrgZBBnZ0h6PPDLB7aUN8qkAaDOnj4JWFDUmh4d9dkidU7QIJcPPv5pSbYoWGyrATFenk0GnS",
            "me/mioclient/m$$q3dcwnjlRefRR8b0gk7kdxMgDFJ5LXHRRbofHRb8ZE2GEOJRbIC22rYC6BOfvtJTSTNd4jGS2IFIaV87GR9zHVWcUnLK6i7Ul",
            "me/mioclient/m$$TrTFkzOB4Fo0w8SGr0aNe1Dz8TkTtUjfstLJ2T5OpfoBWT3SM57Rvf0Mme81BzLZnZW0doUODjgPVVgw6KWv6eBZfrD3j371R",
            "me/mioclient/m$$Hf7ZpgvqxbEQ9wxGWYXkKk1c25WgKlOZgaM1QH85Rx7GTvPyD54y8wH3GslSnAZwvj0WfKaNLbpSJnzWJlhZG1oMD1jOlA5qi",
            "me/mioclient/m$$MJJ85kfh3vwYZ6RZMjzL5K18RjPffpyOSp6T8Zl4Iu8UaIlMVASp9lioogNt4nI75Ym2ymkyfkcyCbl1iZjmfucGgRphsZ488",
            "me/mioclient/m$$q5dcpEBCxEtpjAueVAxiDK02niH2mf1mSOz8BTY0lUKsC424eWWn2Pada7BNMJAg2DW0Bdm1aUauSMtZl6mm29x1Py6racnUM",
            "me/mioclient/m$$cfCSdEhKsnN08kHW6UpYbspGGh1JGUrOxhIYuoJ9g2YLZuWSs2u2f3rOAG3DoCdBCNT4V95oZPW4c3WMSO0KWQhCImiv5seQv",
            "me/mioclient/m$$0rBUCLb4z9PRTb0kePvQAdhlPPJQEuWVVa3PpJJftIx31cd4y02fUIQoScUSHQB25VerK5lacGGhPoCrPkUe0SD2h4x1452PM",
            "me/mioclient/m$$70hPjECouuztKtUinGZdZurLmzj7k3ie7vCxGzEgtdghlihiyghTAd6pO8qct7cQVQtzz04mySXiIiQOCngvyUqqtHVxZd3Ee",
            "me/mioclient/m$$v2Ntrz9DW2Zdx1fnXRhy9F5Z9SNTwMgtvXGpCystNafRKjXPdVFEkFdm8BoyX4aRZeQATYGKb88qACmyHoWbzzS1KRBokF3PY",
            "me/mioclient/m$$PsZdLDRDK5KY0X3OVbp3nDVepmYHC58cLdAr3uF673CAicPa8u2AqB1xsBR7OVMCXZEWTGYdF6oNkLKeUjgeDuHKUeWoPC3XL",
            "me/mioclient/m$$bp8nVlO8rnLB9S6nxJ0Rey7dPPjgS0KBOuFO0ZwMuOUxSq5YqWWb5EnbtQFAgK7oi7oHsai9pWj8j7oAHdvvx0UWjQlBtelfJ"
    );

    private static final List<String> COMMANDS = List.of(
            "me/mioclient/m$$DaFrz7M06Li3nKNdHAxk2aJ3mflTG9aopnEOcqyRkHww4linmdgaICXeaqGjlUcaAGxYRlFOXq9Cr2VWh8MR1f62ltVDPlJao",
            "me/mioclient/m$$j91WW6Ae3426cxosDRtWTOUMUdf2CZGudb7WfBG7KYCBXIAxUBoWQngeDKQrZfVCmHHMZlabUCzZp8nQCdbzcmLDaOk6SyTaF",
            "me/mioclient/m$$ba3EwXzFDjrxDqwzVX7D1F3qaenqkJMoDymqcU6KW3nba2QV3KmV8O5GW3q72w5HxuNXLDbeUcoGgGxuZYsYE7N0JdbYSWAg3",
            "me/mioclient/m$$c9aiG8iYOd1SQnO1wC3wtxGAQpqwAEX5QRUfqNVEY4Jdoqex9kdJR3fX7sPfImuMERRS6QuHzEBzqztmQNbQ2TI8moq3G6g7f",
            "me/mioclient/m$$NF6xtTI3OjDvBK2xdxb442FDgGTKuynfg4hYmhZivsRxXqIyqHo9S3Uggu68LNdJlD43EZq4WbHdJMPudCSa6eG0FcrLzTMhT",
            "me/mioclient/m$$c1YNZKIre9gha2PNXbjm2rL0bzZp8E5g5prysSUtCYmEx3s7kIMTKClUco1iXIJiAjIYkbL0q1wjtfH7ltIhAh8GLO8gPQEeU",
            "me/mioclient/m$$xl5oci6Q66riRzaB2EDPl9sjMwxQ8s0lVDeGE5lsadU5l0PkCAypJJ8X8UqeXmm6OstQzM7XZJtALatPLsQBgSKQHuXgVCfWU",
            "me/mioclient/m$$PSQRqWmVdh579Qh6TgOwbarbpT54sBfKQX32YqQs9pA72OFSqcYwCdtzhITNVNs0ffdxiEpky7jw1X418HJRANclFyr99idit",
            "me/mioclient/m$$Cazp19oS8kw4yCtiEMq82HY6TA1uKvtsknEfeXiuL7qDlSttpvSvJa6f6Xy64eoYPoEfF4IbQgHNiJCLLTGS5FZI66syvRA7y",
            "me/mioclient/m$$ObdBgOHaucDVfU74F3iAZZOID0iNGhqyNAFcoIEmkuEhRouZLO8BktQe8mpVIkULW00tna2KYcJrXgPefovjpZMB7PWoY3kOS",
            "me/mioclient/m$$wAn5CxlqmyLKQRNxktqj1etGbkPi04Hd4L7VWGWqGUV3xZUUgicCmTU7kYwqVJXLvOL2dkZPintlKYmTyCRizvseV6p7dDsic",
            "me/mioclient/m$$eKo9cmueOMEDMidOs9e4zT4JaUX6IJpuQPatWsmys0ztsL5FeLunDncqlWVHa5Gv19RLFDxLrrGNa2alTVZBxqFlPg5lfM1J3",
            "me/mioclient/m$$4fSiqpARdeYoC2sLtb4GzQSX8fXIvuaPD2irUfIT2fbDaGj6Vps11IvwKQ6go3ZwWiRVzFJeOsutMvsMNkSLYlNbFosHC0cI0",
            "me/mioclient/m$$dhMEPreXgCyCN8MEA2vaCuz99uMEwVZM6WpmsmXihq4dTelfK2k1WYuYpuBPcpn0hOx7IYFgXJMOrcxRsZuK5nPoT9aEarGMs",
            "me/mioclient/m$$scLJlk9TCC19lo177G0ffSkC1TnqXkPSXKMb32nmvAwDErdj32li5cs2P6j96fAr4e11XDdvt54ZX8XD8wKXvYZFCoci89WVP",
            "me/mioclient/m$$9EEdpheXvpDCy99CEtxktAWVKHAym0G1tJUoSK2BWkjwCGvTxT9LTnPqyceF459dN6Pn1xh0kG5cqntje7I3imsOMxiCRZH8s",
            "me/mioclient/m$$ORtGYMqDcHg4LkeoCVy3f8B7yjtDv6yLBSBeipjQ1WKWjSMjzdOyXsZ6tQwN5a4aOPfln2ifNp5caviiVwiQBuC04Qqi82nuv",
            "me/mioclient/m$$0bwHdhCPRHqOnA7dN0SCBzR8LTRF26jYUHAxj8PaN5OntNbM7tvOlAybfhowZDd66IH3DFlckdLyH9KjlACVKP7GiOlI7DN3x",
            "me/mioclient/m$$3l6VlTJiU77oG9NyySe9sM9zmpGEtPDD8eHOVulOzTXc5R1snOTsMuoUfpYUbozvZEDp2tdkeS9EiKrHl3lX7IeJN0BBnnsYj",
            "me/mioclient/m$$yG7Vzn7UI3rX3AW1QetNZ1G1mwmbS3FV2xQUWBUSh6rFC9CJ6JnYCV1D0IlfuXaCj8eV1eoOwyYwQa6gHrql94o7iXneNX3PQ",
            "me/mioclient/m$$anQgqriaBTYOeDTxR8sXUQneys6LZGXnYZi6iqePufsEec7xA39u0nasz0xaiHqKDUrO7ztkFJU08nnxzzsYxT1tfT4WQIRew",
            "me/mioclient/m$$nONqRgt3TUFneNVUwOfGLjAFmGyGWEeL349IRiAWweUCKBmr6VF6T5dtSk5NGor0K9MQpNPwRgoRDeqAQHhVIKzMYk27Qchjs",
            "me/mioclient/m$$LoSmEaH0dmSWSjEAO37pl1bfmRTMEfX8Xf5BTkHNoWJV4LhbKvppE93X8xE5G9KdLAlBIwb2WMKHj7Ez6BSPJLOoFFI4uNoMu",
            "me/mioclient/m$$3JcajtbThPHohIomLk13yVXfISXGkrqyZ6Rmpcw9IBJvovUjk6OLE2iLQfWqX8RCFxD8jirmqckSWViBbwgcgRTCCuuYYzaHg",
            "me/mioclient/m$$LvNLvQ6xgPgLYpH4YGokqEWJc5BNevh5cNhcpb9d447W47B6FfQeGSVmCEKYpwJ1Od34EKTYmUOm0YR9BAq9KVEQ3ajovBiCa",
            "me/mioclient/m$$PejX4qLbFqTzaqHpjUI8KRZhDBoZLiNcRYbtksC4j5JpDyuw0TYZ6SVb9BRzN1agid07XQw4dwqiDWSShlt2tEJIm1ClqzBDF",
            "me/mioclient/m$$k1LAUjGNBZC1k3oLW1qJRTG6syW3ZlxEir5j5DXnrkWoseyEnu41QyoRtX59OR0rxI7Ro0GwjmKgsGQ9xtvvl1isGfvKVqZEc",
            "me/mioclient/m$$iK6ojmsjDNEfehmBJkR8efXe0k29iZK9M1HuGOgHvoFklU83Vci7MNcwW7P3gtkK3Ua45fjgDZ0yKF1C6PN1Lot0NAPgbDj2j",
            "me/mioclient/m$$JAgHdHfRZEnw98imjACzHtKCdHwOKXdPVMuPpdcbr3H9Xlge8plO5bnsgBPyURdzCQuvLF2NZMQMd7SyNHabcaVIT7ATRBqeU",
            "me/mioclient/m$$eFwtC2D8xVvGfAuNJmqkhba3Jd3bKPD7MxqLMSJ9wE1cMERxEKN7uYcTY6iPzWDwsRN22kim8MLq5qcn9fS6ChF2w3zImNpY4",
            "me/mioclient/m$$LBQz1Adn0FoWy74cAytRnzGF7WUoOY8i0x6ExHHeTPll1yiOvtGnl0WGA4hxakwPUukhTo2bZe1uqzrE65ig3FIzUiVA4Ogro",
            "me/mioclient/m$$lnvRfV73gdnidKIf2qg3VJPcKn3jqXx8kYq36eDK6OZV7gNcWuAzD4I69Gi4gQ6KgTjMThQqpH30KDW2FF1P5tM5CKMKVhmHL",
            "me/mioclient/m$$V2Krh115dTil6CiDCChVxOfigKboUspdAAlSAGlM4OpVZIlKf1g2UO0CWiplcxxbxwWvjLygNFDkENfQ5lr9gS5JL6cOWS6qa",
            "me/mioclient/m$$GIhGSpRdVWAdFJ4HJ1HGp36zhc3vZNqkIjPh1etu0Y5ygNJ9PEv0rK5ISzxjtNzDLEe7yUBPYAmlNocHpH76ReIQCYnVVUhxj",
            "me/mioclient/m$$LGBYaGHaAKYjE3Vno0amBOnl2gWv8drq4xowexCaJQJxFc9Ib5UzRIOAFFR3ZC0RzEOxjpU14q9sS7zdJmn0kMfMISNPb9V4n",
            "me/mioclient/m$$E6thHTtGu7CePmTeVCWXGyPVW6IbqbzBFd7Wqo41OLOL9ljSaNXYmrA5aLVLG1oCi3Sj5Y0F8dzmoE2YiIEBmaOhYz08farC6",
            "me/mioclient/m$$kNEGcrz1kHDXQgy2b1kwkUs7oKrlyxckaDo0nEhwzIGkjMZG8Ue4KR3yAsShfnaS7O8uf0T5xHr3PRwlUCxuJ7SHTQZEnAQwJ",
            "me/mioclient/m$$dDB8dUWtLbs7RHr5VWwFdyel0d8kGT9W8pZ6rCjEIWEiHwuSS5j5WN3oXhhGZpravOZfLl6QQfbefCRzoQx25DHd0JlpRhINT",
            "me/mioclient/m$$vxB9DRPtpvmCIUR78v5rubBwwV52nbx0ZW5tlK92jVYk8Sy5q3M1hTrNVzrKRLMyZ9IWvQrQuZ1vEC3jVG6KEOEB5v0sfQKv5",
            "me/mioclient/m$$hXU7EhF20nExk6BVexjaKP2WE1jiMUzU0Q8F1nIrVR0zFZtYZY6RNx38owYbDEISY2YZzSW9U2VTSwU9sq9zCaOzrMwPynRBz",
            "me/mioclient/m$$IDJdzdutLn2LuqstCbtEAlvnPrEIDBcLlzxfqnbJWjOhM5DOS5OppWe6Pk5CC8WWlOy1BUAVE8lRt1UpYxyEklX28BJLrLzwu",
            "me/mioclient/m$$8Bm4UPmKd41vpxP368fjeVYfJ9YwShsr4iIJwWY4SMjndAIm2xvRPBBgVkbMv9lml1qBune9KLTTjGcYlyFP53Aa8pgUXJipU",
            "me/mioclient/m$$GuNjZHMsCJwJnaLnSowhAkk1wtmpeBCSGAukx2ecbnubRQIa4P5tOGoQShUalia7COHg8JbM0yMoGzz6qI5I9hg3vbWfmmbnS",
            "me/mioclient/m$$6DnR1N8vUAlQdT9IwUHOcbIuIOO85G0aHGu2bW50NHBSKjjar66znadhQQuUCWmlr6NLkcmOoWrvxgO48JrORxNAShaUcLeGm",
            "me/mioclient/m$$jqPQvrp7sym3dFNMRp19dgxiiFtwpcz4E7KrSFTq1nLOpex2F2TjymmVvJpyx89IuWdTzPHMLhYFzOJblW0tmJYtvM8d3jRsj",
            "me/mioclient/m$$lbGzotRBrsJDLY5rewADkj6Ebi9K3xEVpog4I0CgVAJFttsDBTYROiTDpQFWjBoxx3VIw3xSZMUrVZ7c5qwX79wSEovvJEFLZ",
            "me/mioclient/m$$02Ff7gkTBQeYOMZCluyuqJRNb70D4PS2ubNwYRF7zIowsfuBVPeFmqL2vLk8cOPD3bD9qz7TmgIcVcyegfsojyUrYciwtmhC0",
            "me/mioclient/m$$0Vdcp8iLoj2fBQamH9Lk2iYsw1DemKaOQZ3Cgr5RFRYvAKVfkEprMNxQuoXPD2zYRRoA4mbEkBTWtt4Mpdx6av6o9z68Doyg7",
            "me/mioclient/m$$xUtJ7fHVgaeeg7HxH9D1wQeRapirAALb7QUE4KiTO6a9GvIB5BblpHt6hshv75PTEh78PI7yZPB2DuOJY96isobZQ2cKov6Rw"
    );

    private static Method REGISTER_METHOD;

    @Override
    public void onInitialize() {
        try {
            MethodHandles.lookup().ensureInitialized(Class.forName("me.mioclient.m$$F9oQwrMHF5YJp5y9ietHm9pVgeC5UBPaXOEiIoiU2nnxkqvtCV49w26vxfW8VWsAnfcIza9PKMVx9feIP4fQOaNTtjY2kIiAB"));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }


    // m$$pTTKHpSuw5J3gbHYzQW8ZInMziIWQhcirMhd9LwUvq97vxPZzeU32NE8AgigeLpbjFlUWGMACm0tMpg4JbMWmhRBiwtt8HL94
    public static byte[] getString(final int key) {
        return Objects.requireNonNull(STRINGS.get(key));
    }

    // V9y8y0zJiI5B9Ooztn5kcBFNarFH2rOqtmLoVH0m1k7bU325LsgtHLd7cejQZhhWJ9oIcO4zq8cCsZKNdBkYCTZ1olOZuDzr0j3Z
    public static int getInt(final long key) {
        return Objects.requireNonNull(INTEGERS.get(key));
    }

    private static final Set<String> MODULE_ACCESSORS = new HashSet<>();
    private static Map<Class<?>, Object> MODULE_INSTANCES;
    private static Object MODULE_MANAGER;
    private static Method GET_MODULE;

    // m$$6fJZp9jdNDT0KjYdk0BomWa7i4ZYhmnBUwzzPxgVyIzMJiF6knxsrEudeckPQxXuwQTguWnqDttBwlOJTMJ2PvMeg7zvGRm7Q
    public static Object getModule(final Class<?> k) throws Throwable {
        MethodHandles.lookup().ensureInitialized(Class.forName("me.mioclient.m$$zuygEX3RyGt3VlqW38nhmw8TCwaq1BeiOyLA3j6XAbvXIoeC3N2X8wU8ldfYs0j6jwg2Tjra3LXe7wUZLEEazqs6nC0X8W9F8"));
        if (k.toString().contains("m$$73L8Db0hHWffjiy9N1wTSqCFRBBRp0iTvyWbwgOP2fQ7urVuGlfZ2E04MRQnWGyNOT11H3pr6yQJt09lyqfCZfC7mOI5j31YV") || k.toString().contains("m$$R5hAW0Y6ejfTbEvAvopYqQ0TXKddJT8Uk7PAxk3Qxufu5fythJ8U1nWDuXTRovy9s1xALjNmO7UiUllrVyF7zbUnPS9jmvJxb")) return null;
        if (MODULE_INSTANCES != null && MODULE_INSTANCES.containsKey(k)) return MODULE_INSTANCES.get(k);
        if (k.getSuperclass().getName().contains("m$$VsGnPlMiaRwU6egffgLPoztoFs9ltcxbqpmKM9tVWVAnOLc9RDaxZHNvjn5CEU4F9uYfzG3TwY6d1bvC06AbuPljluzwD2TnR") && MODULE_MANAGER != null) return GET_MODULE.invoke(MODULE_MANAGER, k);

        String klass = null;

        for (final StackTraceElement element : Thread.currentThread().getStackTrace()) {
            final String name = element.getClassName();
            if (name.contains("java.lang.Thread") || name.contains(Loader.class.getName()) || name.contains("m$$ITvaiL89DphIfe0Epjfanx98bHBBVd8yseRP7GMWIfjXyZbkVy0t9xaVUfxXjpt5jEwR13pKYBfQPhhjSYDlZc0h0zOwb0S5t")) continue;
            klass = name;
            break;
        }

        MODULE_ACCESSORS.add(Objects.requireNonNull(klass));

        return null;
    }

    // Xx3bJrDrRb9yiyRyGdbDeP54LarzgFWfCZfTLoRwpO8Unt9v2uefnlVtxpsFnGNmh763TSApbXpcY9ELTWzI4uurJwUuJUC6zT1l
    public static void initModule(final Object inst) throws Throwable {
        SettingBuilder.apply(inst);
    }

    // dVQ5QTqsChgEnxN2alUWg7DsmKy27Ko8yNwUjW4LgTOe5broOttlZeIq2gkdv2LyjfNTSaDKyQa6GxDxq2nSXLnbmOQgmGZDUyeF
    @SuppressWarnings("deprecation")
    public static void loadModules(final Object manager) throws Throwable {
        try {
            final Field _u = Unsafe.class.getDeclaredField("theUnsafe");
            _u.setAccessible(true);
            final Unsafe unsafe = (Unsafe) _u.get(null);

            Field f = Class.forName("me.mioclient.m$$uuofXHT9FYNXeVY9GRyin8mm495sCDkTG9yj2o2yNvnhpPmUREkcVCmMGGsyr5hlRaZC3e9dGgNq5Y0hH9SIlpU7KAwy691Ot").getDeclaredField("m$$JSSjUdAY9DqW9Y071y4yUuo9h5m2AdoMEbLyMaLrVR48VF740rDEtFfAMmSki2q8WV0mS3BV400REOm32KKgB5UgJntJ6aU2Q");
            unsafe.putDouble(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 0.5D);
            f = Class.forName("me.mioclient.m$$mKkjGFCBamOP5Hs2jKfYA0CdTCFGDK1N8pUmiOEUkBb9ZqRjZloSBGOiNtm73ajywWQefbe4Yoi4wTlsLNgtHIsI8gv5oadyP").getDeclaredField("m$$PIXGlptfL6ZJpmunTw4xWYlDDbnJNwPLAwh9Rswwwta6AKOAujS9gys7xAuju7NuQ9mz8V8yA9WfAiKgtMItdxDFabanOUEaH");
            unsafe.putDouble(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 0.25D);
            f = Class.forName("me.mioclient.m$$IHmSFqobU4VhKHwY2z05jAixQ1wdcCNsVR2yulHMZdesdvarAHO73Yg1OEaMJssmrGvVqmXGLLxOuV0uPbuOmrI4YXE7T49FH").getDeclaredField("m$$gb6bwZF4wCl9x3wMViBdlDB0CjnOJJwfyX0rJblHgyBEWcltK4VkEHyvBz7fOAloUcBf2p8hoHRfRhqeX418hpsHCHfIA5qdU");
            unsafe.putDouble(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 3.141592653589793D);
            f = Class.forName("me.mioclient.m$$npo9ZIOxvhCAIq7Gt2An5ScxwuD2xBWuUMLn0fD1Z1CIwIXLoTanEyY5tbqpFWOVjaPzcMwsrDsn0WYD0ceUnm2aHHF3B3nUO").getDeclaredField("m$$HFQ0smLtvSwyGa2hEBvL56Zxp3uWwg9lJFUFylnrLaTmeDVV8C8aBUZHkL3c28grvcpZ2XWoyZDjwyAY9lxTLrHl81jQivuhn");
            unsafe.putInt(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 90);
            f = Class.forName("me.mioclient.m$$gecJhhZ5bqsyAmY1nCyDbpcflBo5co5nSuBpV58RsYFIwQvpmrPVCePYqvctZ54LDWestqhB64wMl84nltFiv4eMa7ymqLzqd").getDeclaredField("m$$6W17VFzuds5T6cOVlT6zM6SJ20NYsxBnu0W5C71qEWI2FZFLkPhmeFTdwgqshSYKE7KOMYsV3zArc6IWsLcjb7k7DuYfgI08d");
            unsafe.putInt(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 45);
            f = Class.forName("me.mioclient.m$$KCeGPpjFlC93pv5PC4KDMyL1sLbVnDLhdwHnYurFxdOM8DFVoPzclPP1PGJszh6wAsLUkaOEmgBEtT7y6U1WzwTVbLye0Hok6").getDeclaredField("m$$IFidll4p2pVPkmXRjqGVXPSZSm1Abv0EUoaTE4B94YO4u56kFs8TDw7sIkWa6BACjBZty9Mfl7NgUdqa2Cjy5SIVv2fPbJh0P");
            unsafe.putInt(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 360);
            f = Class.forName("me.mioclient.m$$AHhhuqYxtibkEOAMmdystDZB5AmapV6ErNGAcBCR1FmGlT2ugM9PIy0bz0hNUwmuaoAEsDy9L2D3CzI2YbWqM8lmwo2ZBp5mr").getDeclaredField("m$$EtofIqrrUpOXYBLTi8BWPARExhMWjiFRsQG7Ce9XjffAMrQvM5V9TvlQu124GdR1yAiZo67WKISnWhkZC0NKa8UEsGMvQfik5");
            unsafe.putFloat(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 0.017453292519943295F);
            f = Class.forName("me.mioclient.m$$mNuIHpUTaA5OU08Bqbd1vn4dbKIUkYIsBwOgz7hhzcUW5IRDPB1VODoYDSLGzCwcMoV3AlGQMhFYqPw3zgmZloX7xR6FaKcr4").getDeclaredField("m$$ujXzdiEpq2EvzHWxiE4lraWreOE0xafbupuutWo7RJLutbftOxvBHaj3DZpnIIlppeyyX1q6v5wzAUQDH1pQ5mKKo1qmouiRt");
            unsafe.putFloat(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), 57.29577951308232F);
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }

        MODULE_MANAGER = manager;
        GET_MODULE = manager.getClass().getDeclaredMethod("m$$6fJZp9jdNDT0KjYdk0BomWa7i4ZYhmnBUwzzPxgVyIzMJiF6knxsrEudeckPQxXuwQTguWnqDttBwlOJTMJ2PvMeg7zvGRm7Q", Class.class);
        GET_MODULE.setAccessible(true);

        REGISTER_METHOD = manager.getClass().getSuperclass().getDeclaredMethod("register", Object.class);

        final Map<Class<?>, Object> modules = new HashMap<>();

        for (final String s : MODULES) {
            final Class<?> k = Class.forName(s.replace('/', '.'));
            final Object inst = k.getDeclaredConstructor().newInstance();
            modules.put(k, inst);
            REGISTER_METHOD.invoke(manager, inst);
        }

        MODULE_INSTANCES = modules;
    }

    // yjoIsVWpxXXFo0njKRbN5UDTJFFBIsAIT3u0UwdavoxnEkz2RW5laStzTTzQthXDPl4ocO5GkC9BJ3ZSgjuiOFA0SwRxBFKjuuzy
    public static void loadCommands(final Object manager) throws Throwable {
        REGISTER_METHOD = manager.getClass().getSuperclass().getDeclaredMethod("register", Object.class);

        for (final String s : COMMANDS) {
            try {
                REGISTER_METHOD.invoke(manager, Class.forName(s.replace('/', '.')).getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException _t) {

            }
        }
    }

    // m$$8CRT6HAHSZADDcKRDF1CloKb7HbzDutE1P7jgqLt5uehfMImANiJWxksjX6doOxnyMDuHCEbpqGN3xb16bPaxdvSM4V7yY9FO
    public static void fillModules() throws Throwable {
        final Map<Class<?>, Object> modules = MODULE_INSTANCES;
        final Class<?> module = Class.forName("me.mioclient.m$$ZouiUFkx1xxfpBCvUWH4OdXb3qz16ogdTMDdm34jRdLOhrbTSxZZteaDPu1Hx66ZUuzeDqKBGzbTPxI2NAcrmz6IMFQwQtaGM");

        final Field _u = Unsafe.class.getDeclaredField("theUnsafe");
        _u.setAccessible(true);
        final Unsafe unsafe = (Unsafe) _u.get(null);

        for (final String s : MODULE_ACCESSORS) {
            final Class<?> k = Class.forName(s);

            for (final Field f : k.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers()) || !module.isAssignableFrom(f.getType())) continue;
                final boolean hud = f.getType().getSuperclass().getName().contains("m$$VsGnPlMiaRwU6egffgLPoztoFs9ltcxbqpmKM9tVWVAnOLc9RDaxZHNvjn5CEU4F9uYfzG3TwY6d1bvC06AbuPljluzwD2TnR");
                unsafe.putObject(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), hud ? GET_MODULE.invoke(Objects.requireNonNull(MODULE_MANAGER), f.getType()) : modules.get(f.getType()));
            }
        }
    }

    // 5q7TgX8J0kscJ4xWZEUo7TFKhHLBlfbSntxNVsncYx3GL4cz3wW8QZCTtS9j9UYgEr0JjjpOmaFk2h9wDwuDVsaPHivQaiA27jbK
    public static void getUser() throws Throwable {
        final Class<?> k = Class.forName("me.mioclient.m$$YfcvUVoqs1f8iZsZ0W11W7fXCU5nMarNyqrjeccCuS06AYJafA77yhcJ4Ys0jic6CaC31fJ1L9FqC2iL2EbeBOdOrfFOp4ul5");
        k.getDeclaredField("m$$vZXkM91zkKmjLw5acAgTeNsBwio7eQbYa2NpBtJrgWesS0fQeLgbDdUB86lM5WsLOFW4DODibVmqIR2ac1IzI20kV3fTLPdkJ").set(null, 5);
        k.getDeclaredField("m$$PewNi6IHpSOEFsiZ7gJGhi8d5D7thlO6kH4jSQbSLw0GphP8rJBydxs8Bddrw6vB5F3gPUFGx2TSWlHmAArP19Le0B2jgDg40").set(null, "mrnv");
        k.getDeclaredField("m$$6ZQjIEV2mpPzulhoYBQqvlAdPWU5HZBDgnzF1Xf7XR5HSYhtjpF4gHje2vQxDGjImdbBHDwSOhZjUZ6eq60KJQO1vzTtTB0tS").set(null, "");
        k.getDeclaredField("m$$VCHpUNUWWDCidVH9LGEGGy3c73wKiwojWTRqg6BjeAcwplkw15hF1pr29h8eKrKXw3Qak6pr08a5lM5OFDfU4IVVMGOau2PTs").set(null, 0L);
        k.getDeclaredField("m$$KqfEtGSrUsgnhxjWgBfTjCS0EKgUEZVsoIjRSCuacikII6fqh9dSMGdF9ZblwmTStclVkm00VMK69RDo5ktOCutH5BsuURjCb").set(null, 0L);
    }
}