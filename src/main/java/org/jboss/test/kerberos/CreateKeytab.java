/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.kerberos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.directory.server.kerberos.shared.crypto.encryption.KerberosKeyFactory;
import org.apache.directory.server.kerberos.shared.keytab.Keytab;
import org.apache.directory.server.kerberos.shared.keytab.KeytabEntry;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

/**
 * Helper utility for creating Keytab files.
 * 
 * @author Josef Cacek
 */
public class CreateKeytab {

    // Public methods --------------------------------------------------------

    /**
     * The main.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 3) {
            System.out.println("Kerberos keytab generator");
            System.out.println("-------------------------");
            System.out.println("Usage:");
            System.out.println("java -classpath kerberos-using-apacheds.jar " + CreateKeytab.class.getName()
                    + " <principalName> <passPhrase> <outputKeytabFile>");
        } else {
            final File keytabFile = new File(args[2]);
            createKeytab(args[0], args[1], keytabFile);
            System.out.println("Keytab file was created: " + keytabFile.getAbsolutePath());
        }
    }

    /**
     * Creates a keytab file for given principal.
     * 
     * @param principalName
     * @param passPhrase
     * @param keytabFile
     * @throws IOException
     */
    public static void createKeytab(final String principalName, final String passPhrase, final File keytabFile)
            throws IOException {
        final KerberosTime timeStamp = new KerberosTime();
        final long principalType = 1L; //KRB5_NT_PRINCIPAL

        final Keytab keytab = Keytab.getInstance();
        final List<KeytabEntry> entries = new ArrayList<KeytabEntry>();
        for (Map.Entry<EncryptionType, EncryptionKey> keyEntry : KerberosKeyFactory.getKerberosKeys(principalName, passPhrase)
                .entrySet()) {
            final EncryptionKey key = keyEntry.getValue();
            final byte keyVersion = (byte) key.getKeyVersion();
            entries.add(new KeytabEntry(principalName, principalType, timeStamp, keyVersion, key));
        }
        keytab.setEntries(entries);
        keytab.write(keytabFile);
    }
}
