enum PrintType {
  byte,
  text,
  pdf,
  qr,
  walkPaper,
}

enum PrintAlignment {
  left,
  center,
  right,
}

enum PrintedFontSize {
  size18,
  size24,
  size34,
  size44,
  size54,
  size64,
}

// TODO: describe each status
enum TelpoStatus {
  ok,
  noPaper,
  overHeat,
  cacheIsFull,
  unknown,
}
