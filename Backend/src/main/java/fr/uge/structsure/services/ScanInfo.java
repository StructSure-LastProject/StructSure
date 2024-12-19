package fr.uge.structsure.services;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Scan;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScanInfo{
    private final Scan scan;
    private final List<Result> results;

    public ScanInfo(Scan scan, List<Result> results) {
      this.scan = Objects.requireNonNull(scan);
      this.results = Objects.requireNonNull(Collections.unmodifiableList(results));
    }
  }