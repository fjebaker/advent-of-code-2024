# Advent of Code 2024

I'm solving the [Advent of Code](https://adventofcode.com/) as a way of learning new programming languages. This year I'm doing it in [Clojure](https://www.clojure.org/) to learn another lispy language!

## Lessons learned

- 01: `(frequencies some-list)` is an amazing builtin. Also I miss Scheme's playful `(string->int)` style parsing functions. `(Integer/parseInt)` feels too professional.
- 02: You can do keyword arguments with map-destructuring, but they are not checked for validity? As in, if my keyword is `:damped?` but someone passes `:damped`, it just silently fails.

