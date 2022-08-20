package com.toutiao.melon.workerprocess.job;

import com.toutiao.melon.api.IJob;
import com.toutiao.melon.api.IOutStream;
import com.toutiao.melon.api.job.Job;
import com.toutiao.melon.api.job.Node;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class OperatorLoader {

    @SuppressWarnings("unchecked")
    public Class<? extends IOutStream> load(URL jarLocalUrl, String taskName) throws Throwable {
        URL[] url = {jarLocalUrl};
        try (URLClassLoader loader = URLClassLoader.newInstance(url);
             JarFile jarFile = new JarFile(jarLocalUrl.getFile())) {
            loadAllClasses(jarFile.entries(), loader);
            String mainClassName = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
            Class<?> mainClass = loader.loadClass(mainClassName);
            Job job = ((IJob) mainClass.newInstance()).getJob();
            Map<String, Node> nodes = job.getNodes();

            String operatorClassName = nodes.get(taskName).getClassName();
            return (Class<? extends IOutStream>) loader.loadClass(operatorClassName);
        }
    }

    private void loadAllClasses(Enumeration<JarEntry> jarEntries, URLClassLoader cl)
            throws ClassNotFoundException {
        while (jarEntries.hasMoreElements()) {
            JarEntry je = jarEntries.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of ".class"
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            cl.loadClass(className);
        }
    }
}
