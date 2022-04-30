# cljyenc

A Clojure library to encode binary data into YEnc and decode
text encoded in YEnc. The libraries name is pronouned (See-El-Jenk)

## Usage

The niche use of Yenc
is to encode binary data into strings that won't confuse text based
protocols of network applications.This library takes bytes and encodes them into a string using YEnc. My own usage is to encode bytes into String and back, since the built in Java character
encodings aren't able to consistently and accurately reproduce byte
streams from input strings in my experience. YEnc was originally
used as a replacement for UUEncode on USENET. MIME has 
superseeded text based protocols for a while. 

## Analysis

The following is a description of the YEnc algorithm. It is a very simple
procedure. YEnc relies on using most of the ASCII character set. It's 
historical use was for encoding USNET messages. We provide the basic
algorithm in pseudocode.

### Encoding

You YEnc data by adding 42 to each byte modulo 256 (after adding 42, it
rolls over to 0). 

```
with each byte in byte-array:
    add 42 to byte mod 256, place in x
    switch on x:
        case 0, 9, 10, 13, 61: output "=" and x + 64
        default: output x
```

### Decode

To decode text into binary you do the following.

```
with each char in character-array:
    let x = ordinal value of char
    let read-next-as-critical-flag = false
    switch on x:
        case read-next-as-critical-flag: output x - 64 - 42 (or - 106)
        case 61: read next character as critical (set read-next-as-critical-flag to true)
        default: output x - 42
```

### Related Java Caveats

In Java `byte` is signed, so we must must promote and convert `byte`s to unsigned `int`s
with the static method `Byte.toUnsignedInt()` to coerce the right numbers out of 
the computations. To change the integer back we use the instance method of 
`Integer`, `.byteValue()` which returns the unsigned integer to a signed version.

### Known Flaws and Limitations

The library does not detect the `=yend`. This doesn't matter; removing the 
last bytes of the decode works. The epilogue code looks like its detecting 
the header instead. None of this impacts the basic function of encoding
and decoding YEnc.

## License

Copyright © 2022 Andy Vogel

This program is released under the MIT License. It is free software. This software is 
distributed as-is with no warranty. See LICENSE for more details.

