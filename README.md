# WyldCard

[Features](#features) | [Getting Started](#getting-started) | [Building](doc/BUILDING.md) | [Scripting Guide](https://github.com/defano/wyldcard/wiki)

A reproduction of Apple's HyperCard, written in Java. Originally developed as a class project for a graduate-level compiler design course at DePaul University in Chicago. [Refer to the wiki](https://github.com/defano/wyldcard/wiki) for details.

[![Build Status](https://travis-ci.org/defano/wyldcard.svg?branch=master)](https://travis-ci.org/defano/wyldcard) [![Sonar](https://sonarcloud.io/api/project_badges/measure?project=wyldcard&metric=alert_status)](https://sonarcloud.io/dashboard?id=wyldcard)

![WyldCard](doc/images/hero.png)

## Features

WyldCard strives to offer a high-fidelity reproduction of Apple's original HyperCard, not a modernization of it:

* Cards support foreground and background layers; buttons and fields come in a variety of styles similar to HyperCard's; text fields can hold richly-styled text. Multiple stacks can be opened simultaneously in different windows.
* Paint and draw using all the original paint tools, patterns and image transforms (provided by the [JMonet library](https://www.github.com/defano/jmonet)). WyldCard supports full-color graphics and alpha transparency.
* Attach HyperTalk scripts to buttons, fields, cards, backgrounds and stacks; control the menu bar and address windows as objects. Most aspects of the HyperTalk 2.4.1 language have been implemented, including chunk expressions, message passing and context-sensitive evaluation of object factors. Sports a built-in step debugger with variable watching, message watching and in-context script execution.
* Compose music using HyperCard's original sound effects (`flute`, `harpsichord` and `boing`); `dial` telephone numbers; `speak` text; and animate cards with one of 23 visual effects provided by the [JSegue library](https://www.github.com/defano/jsegue).

#### What's missing?

* WyldCard can't open or import old HyperCard stacks and has no concept of external commands or functions (XCMDs/XFCNs).
* No home stack; no concept of user levels; no ability to inherit behavior from other stacks (`start using ...`).

## Getting started

Getting started is easy. What is it that you're interested in doing?

#### I want to download and play with this.

Someday an executable will be available for download, until then, see the [build guide](doc/BUILDING.md) for instructions on how to run the application using Gradle.

#### I'm a Java developer and want to contribute to the source code.

Glad to have you aboard! Have a look at [the build instructions](doc/BUILDING.md).

#### I want to run the real HyperCard.

Use the SheepShaver emulator to run Macintosh System Software on modern Macs and PCs. See [this tutorial](https://jamesfriend.com.au/running-hypercard-stack-2014) for details.

#### I'm an attorney looking for new work.

This project represents a homework assignment gone awry and is in no way associated with Apple's long-obsolete HyperCard application program. HyperCard&trade;, HyperTalk&trade; and any other trademarks used within are the property of FileMaker, Inc., Apple, Inc. and/or their rightful owner(s).

## Remind me, what's HyperCard?

Apple called HyperCard "programming for the rest of us." Steve Wozniak called it ["the best program ever written"](https://www.macworld.com/article/1018126/woz.html).

Released in 1987 and included in the box with every Macintosh sold during the late 1980's and '90s. HyperCard was a software [Erector Set](https://en.wikipedia.org/wiki/Erector_Set): part programming language, part paint program, part database. With HyperCard, you could draw a user interface with [MacPaint](https://en.wikipedia.org/wiki/MacPaint)-like tools, then apply scripts and behaviors to it with an expressive syntax that mimicked natural English.

Need more? [Watch an interview of HyperCard's creators](https://www.youtube.com/watch?v=BeMRoYDc2z8) Bill Atkinson and Dan Winkler on The Computer Chronicles, circa 1987.

#### Nu ar det slut...
