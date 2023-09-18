#!/bin/zsh

RED="\033[31m"
GREEN="\033[32m"
RESET="\033[0m"

CC="$1"
SOURCE="$2"
OUTPUT=
TIMEOUT=2s

function colorized_echo() {
  local color="$1"
  local message="$2"
  echo -e "${color}${message}${RESET}"
}

function preprocess() {
  if [ -z "$CC" ]; then
    CC=xcc
    colorized_echo "$GREEN" "Default CC used: $CC"
  fi
  if [ -z "$SOURCE" ]; then
    SOURCE=a.c
    colorized_echo "$GREEN" "Default source file used: $SOURCE"
  fi
}

function generate_output_name() {
  if echo "$1" | grep "\." 2>&1 > /dev/null; then
    OUTPUT=$(echo "$1" | sed 's/\.[[:alnum:]]*$/.s/')
  else
    OUTPUT="$1.s"
  fi
}

function compile() {
  if ! eval "$CC -o $OUTPUT $SOURCE"; then
    colorized_echo "$RED" "Compilation failed."
    exit -1
  fi
}

function execute() {
  if ! gtimeout $TIMEOUT java -jar /opt/Mars.jar nc $OUTPUT <input.txt >output.txt; then
    colorized_echo "$RED" "Timeout."
    exit -2
  fi
  colorized_echo "$RED" "Running successfully."
}

function dump_output() {
  echo "Output:"
  cat output.txt
}

function main() {
  preprocess
  generate_output_name "$SOURCE"
  compile
  execute
  dump_output
}

main