/*
 * Copyright (c) 2014-2015 Simplestream Ltd
 * All Rights Reserved.
 */

package uk.me.jeffsutton.worldremitdemo.ssl;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;

import uk.me.jeffsutton.worldremitdemo.R;

/**
 * An interface to the system's trust anchors. We're using our own truststore, which is just the AOSP default, but in a
 * place we know to find it for sure.
 * 
 * @author Moxie Marlinspike
 */
public class SystemKeyStore {

	private static SystemKeyStore instance;

	private static final int CACERTS_FILE_SIZE = 1024 * 4;

	public static synchronized SystemKeyStore getInstance(Context context) {
		if (instance == null) {
			instance = new SystemKeyStore(context);
		}
		return instance;
	}

	private final HashMap<Principal, X509Certificate> trustRoots;
	final KeyStore trustStore;

	private SystemKeyStore(Context context) {
		final KeyStore trustStore = getTrustStore(context);
		this.trustRoots = initializeTrustedRoots(trustStore);
		this.trustStore = trustStore;
	}

	public boolean isTrustRoot(X509Certificate certificate) {
		final X509Certificate trustRoot = trustRoots.get(certificate.getSubjectX500Principal());
		return trustRoot != null && trustRoot.getPublicKey().equals(certificate.getPublicKey());
	}

	public X509Certificate getTrustRootFor(X509Certificate certificate) {
		final X509Certificate trustRoot = trustRoots.get(certificate.getIssuerX500Principal());

		if (trustRoot == null) {
			return null;
		}

		if (trustRoot.getSubjectX500Principal().equals(certificate.getSubjectX500Principal())) {
			return null;
		}

		try {
			certificate.verify(trustRoot.getPublicKey());
		} catch (GeneralSecurityException e) {
			return null;
		}

		return trustRoot;
	}

	private HashMap<Principal, X509Certificate> initializeTrustedRoots(KeyStore trustStore) {
		try {
			final HashMap<Principal, X509Certificate> trusted = new HashMap<Principal, X509Certificate>();

			for (Enumeration<String> aliases = trustStore.aliases(); aliases.hasMoreElements();) {
				final String alias = aliases.nextElement();
				final X509Certificate cert = (X509Certificate) trustStore.getCertificate(alias);

				if (cert != null) {
					trusted.put(cert.getSubjectX500Principal(), cert);
				}
			}

			return trusted;
		} catch (KeyStoreException e) {
			throw new AssertionError(e);
		}
	}

	private KeyStore getTrustStore(Context context) {
		try {
			final KeyStore trustStore = KeyStore.getInstance("BKS");
			final BufferedInputStream bin = new BufferedInputStream(context.getResources().openRawResource(
					R.raw.keystore), CACERTS_FILE_SIZE);

			try {
				trustStore.load(bin, "demo".toCharArray());
			} finally {
				try {
					bin.close();
				} catch (IOException ioe) {
					Log.w("SystemKeyStore", ioe);
				}
			}

			return trustStore;
		} catch (KeyStoreException kse) {
			throw new AssertionError(kse);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		} catch (CertificateException e) {
			throw new AssertionError(e);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

}
