package net.impactdev.impactor.api.dependencies;

import com.google.common.io.ByteStreams;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.utilities.mappings.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public enum DependencyRepository {

    IMPACTDEV("https://maven.impactdev.net/repository/development/", new Tuple<>(3, "https://maven.impactdev.net/service/rest/v1/search/assets/download?repository=development&")) {
        @Override
        protected URLConnection openConnection(Dependency dependency) throws IOException {
            URLConnection connection = super.openConnection(dependency);
            connection.setRequestProperty("User-Agent", "impactor");

            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            return connection;
        }
    },
    /**
     * The primary ImpactDev repository, with an additional Maven Repo mirror.
     *
     * <p>This is used to reduce the load on repo.maven.org</p>
     */
    // Please ask me (NickImpact) before using this mirror in your own project.
    IMPACTDEV_MIRROR("https://maven.impactdev.net/repository/maven-central/") {
        @Override
        protected URLConnection openConnection(Dependency dependency) throws IOException {
            URLConnection connection = super.openConnection(dependency);

            connection.setRequestProperty("User-Agent", "impactor");

            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            return connection;
        }
    },
    /**
     * The official repository for Aikar based products as well as other utilities
     */
    AIKAR("https://repo.aikar.co/nexus/content/groups/aikar/", new Tuple<>(-1, "https://repo.aikar.co/nexus/service/local/artifact/maven/redirect?r=aikar&")) {
        @Override
        protected URLConnection openConnection(Dependency dependency) throws IOException {
            URLConnection connection = super.openConnection(dependency);

            connection.setRequestProperty("User-Agent", "impactor");

            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            return connection;
        }
    },
    /**
     * Maven Central.
     */
    MAVEN_CENTRAL("https://repo1.maven.org/maven2/"),
    ;

    private final String url;
    private final Tuple<Integer, String> snapshots;

    DependencyRepository(String url) {
        this.url = url;
        this.snapshots = null;
    }

    DependencyRepository(String url, Tuple<Integer, String> snapshots) {
        this.url = url;
        this.snapshots = snapshots;
    }

    protected URLConnection openConnection(Dependency dependency) throws IOException {
        if(dependency.isSnapshot() && this.snapshots != null && this.snapshots.getFirst() > 0) {
            URL url;
            if(this.snapshots.getFirst() == 2) {
                url = new URL(String.format(
                        "%sg=%s&a=%s&v=LATEST",
                        this.snapshots.getSecond(),
                        dependency.getGroup(),
                        dependency.getArtifact()
                ));
            } else {
                url = new URL(String.format("%sgroup=%s&name=%s&sort=version", this.snapshots.getSecond(), dependency.getGroup(), dependency.getArtifact()));
            }
            return url.openConnection();
        } else {
            URL dependencyURL = new URL(this.url + dependency.getMavenPath());
            return dependencyURL.openConnection();
        }
    }

    public byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException {
        try {
            URLConnection connection = openConnection(dependency);
            try(InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if(bytes.length == 0) {
                    throw new Exception("empty stream");
                }

                return bytes;
            }
        } catch (Exception e) {
            throw new DependencyDownloadException(e);
        }
    }

    public byte[] download(Dependency dependency) throws DependencyDownloadException {
        byte[] bytes = downloadRaw(dependency);

        if(!dependency.isSnapshot()) {
            // compute a hash for the downloaded file
            byte[] hash = Dependency.createDigest().digest(bytes);

            // ensure the hash matches the expected checksum
            if (!dependency.checksumMatches(hash)) {
                throw new DependencyDownloadException("Downloaded file had an invalid hash. " +
                        "Expected: " + Base64.getEncoder().encodeToString(dependency.getChecksum()) + " " +
                        "Actual: " + Base64.getEncoder().encodeToString(hash));
            }

            Impactor.getInstance().getRegistry().get(ImpactorPlugin.class).getPluginLogger().info("Successfully downloaded '" + dependency.getFileName() + ".jar' with matching checksum: " + Base64.getEncoder().encodeToString(hash));
        } else {
            Impactor.getInstance().getRegistry().get(ImpactorPlugin.class).getPluginLogger().info("Successfully downloaded '" + dependency.getFileName() + ".jar'");
        }

        return bytes;
    }

    public void download(Dependency dependency, Path file) throws DependencyDownloadException {
        try {
            Files.write(file, download(dependency));
        } catch (IOException e) {
            throw new DependencyDownloadException(e);
        }
    }

}
