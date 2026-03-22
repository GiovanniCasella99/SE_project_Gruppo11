package com.unisa.seproject.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Scans well-known Windows program directories and returns a flat, sorted list
 * of {@code .exe} files so the frontend can show a searchable program picker
 * without requiring the user to navigate the filesystem manually.
 */
@RestController
@RequestMapping("/api/fs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FileBrowserController {

    private static final List<Path> SEARCH_ROOTS = List.of(
            Path.of(System.getProperty("user.home"), "AppData", "Local", "Microsoft", "WindowsApps")
    );

    private static final int MAX_DEPTH = 3;

    @GetMapping("/programs")
    public List<ProgramEntry> listPrograms() {
        List<ProgramEntry> results = new ArrayList<>();
        for (Path root : SEARCH_ROOTS) {
            if (Files.exists(root)) {
                scan(root, 0, results);
            }
        }
        results.sort(Comparator.comparing(e -> e.name().toLowerCase()));
        return results;
    }

    private void scan(Path dir, int depth, List<ProgramEntry> results) {
        if (depth > MAX_DEPTH) return;
        try (var stream = Files.list(dir)) {
            stream.forEach(path -> {
                try {
                    if (Files.isDirectory(path)) {
                        scan(path, depth + 1, results);
                    } else if (path.getFileName().toString().toLowerCase().endsWith(".exe")) {
                        results.add(new ProgramEntry(path.getFileName().toString(), path.toString()));
                    }
                } catch (SecurityException ignored) { /* skip protected entries */ }
            });
        } catch (IOException | SecurityException ignored) { /* skip unreadable dirs */ }
    }

    public record ProgramEntry(String name, String path) {}
}
