package com.horace.coin.network;

import lombok.SneakyThrows;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.random.RandomGenerator;

public class AdvancedNSLookup {

    private static Hashtable<String, String> env = new Hashtable<>();
    private static DirContext ctx;

    static {
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns://8.8.8.8/"); // Google's DNS
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static String[] getAddress(String hostname) {
        Attributes attrs = ctx.getAttributes(hostname, new String[]{"A"});
        List<String> addresses = new ArrayList<>();
        attrs.get("A").getAll().asIterator().forEachRemaining(attr -> addresses.add(attr.toString()));
        return addresses.toArray(new String[0]);
    }

    public static String pickNode() {
        String[] addrs = getAddress("seed.bitnodes.io");
        return addrs[RandomGenerator.getDefault().nextInt(0, addrs.length)];
    }

    public static void main(String[] args) {
        String hostname = "seed.bitnodes.io";
        String[] addrs = getAddress(hostname);
        System.out.println(String.join(",", addrs));
        /*
        try (DirContext ctx = new InitialDirContext(env)) {

            String[] recordTypes = {"A", "MX", "TXT", "NS", "CNAME"};

            System.out.println("DNS Records for " + hostname + ":");
            for (String type : recordTypes) {
                try {
                    Attributes attrs = ctx.getAttributes(hostname, new String[]{type});
                    System.out.println("\n" + type + " Records:");
                    System.out.println(attrs.get(type));
                } catch (NamingException e) {
                    // Record type not found
                }
            }
        } catch (NamingException e) {
            System.out.println("DNS lookup failed: " + e.getMessage());
        }
         */
    }

}
