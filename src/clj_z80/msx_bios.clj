(ns clj-z80.msx-bios)


;; from http://map.grauw.nl/resources/msxbios.php


(def CHKRAM
  "Function : Tests RAM and sets RAM slot for the system
  Registers: All
  Remark   : After this, a jump must be made to INIT, for further initialisation."
  0x0000)

(def SYNCHR
  "Function : tests whether the character of [HL] is the specified character
           if not, it generates SYNTAX ERROR, otherwise it goes to CHRGTR (#0010)
Input    : set the character to be tested in [HL] and the character to be
           compared next to RST instruction which calls this routine (inline parameter)
Output   : HL is increased by one and A receives [HL], When the tested character is
           numerical, the CY flag is set the end of the statement (00h or 3Ah) causes
           the Z flag to be set
Registers: AF, HL"
  0x0008)

(def RDSLT
  "Function : Reads the value of an address in another slot
Input    : A  - ExxxSSPP
           |        || Primary  slotnumber  (00-11)
           |        - Secundary slotnumber (00-11)
           +----------- Expanded slot (0 = no, 1 = yes)
           HL - Address to read
Output   : A  - Contains the vaule of the read address
Registers: AF, C, DE
Remark   : This routine turns off the interupt, but won't turn it on again"
  0x000C)

(def CHRGTR
  "Function : Gets the next character (or token) of the Basic-text
Input    : HL - Address last character
Output   : HL - points to the next character
           A  - contains the character
           C  - flag set if it's a number
           Z  - flag set if it's the end of the statement
Registers: AF, HL"
  0x0010)

(def WRSLT
  "Function : Writes a value to an address in another slot.
Input    : A  - Slot in which the value will be written
           see RDSLT for input
           HL - Address of value to write
           E  - value to write
Registers: AF, BC, D
Remark   : See RDSLT"
  0x0014)

(def OUTDO
  "Function : Output to current outputchannel (printer, diskfile, etc.)
Input    : A  - PRTFIL, PRTFLG
Remark   : Used in basic, in ML it's pretty difficult"
  0x0018)

(def CALSLT
  "Function : Executes inter-slot call.
Input    : IY - High byte with input for A in RDSLT
           IX - The address that will be called
Remark   : Variables can never be given in alternative registers
           of the Z-80 or IX and IY"
  0x001C)

(def DCOMPR
  "Function : Compares HL with DE
Input    : HL, DE
Output   : Z-flag set if HL and DE are equal. C-flag set if HL is less than DE.
Registers: AF"
  0x0020)

(def ENASLT
  "Function : Switches indicated slot at indicated page on perpetual
Input    : A  - ExxxSSPP
                +-?------ see RDSLT
           H - Bit 6 and 7 must contain the page number (00-11)"
  0x0024)

(def GETYPR
  "Function : Returns Type of DAC
Input    : DAC
Output   : S,Z,P/V, CY
Registers: AF
Remark   : Not a very clear routine to me, please mail us if you know more about it."
  0x0028)

(def CALLF
  "Function : Executes an interslot call
Output   : depends on the calling routine
Registers: AF, and the other registers depending on the calling routine
Remark   : The following is the calling sequence:
           RST #30
           DB destination slot (see RDSLT accu)
           DW destination address"
  0x0030)

(def KEYINT
  "Function : Executes the timer interrupt process routine"
  0x0038)

(def INITIO
  "Function : Initialises the device
Registers: All"
  0x003B)

(def INIFNK
  "Function : Initialises the contents of the function keys
Registers: All"
  0x003E)

(def DISSCR
  "Function : inhibits the screen display
Registers: AF, BC"
  0x0041)

(def ENASCR
  "Function : displays the screen
Registers: AF, BC"
  0x0044)

(def WRTVDP
  "Function : write data in the VDP-register
Input    : B  - data to write
           C  - number of the register
Registers: AF, BC"
  0x0047)

(def RDVRM
  "Function : Reads the content of VRAM
Input    : HL - address read
Output   : A  - value which was read
Registers: AF"
  0x004A)

(def WRTVRM
  "Function : Writes data in VRAM
Input    : HL - address write
           A  - value write
Registers: AF"
  0x004D)

(def SETRD
  "Function : Enable VDP to read
Input    : HL - for VRAM-address
Registers: AF"
  0x0050)

(def SETWRT
  "Function : Enable VDP to write
Input    : HL - Address
Registers: AF"
  0x0053)

(def FILVRM
  "Function : fill VRAM with value
Input    : A  - data byte
           BC - length of the area to be written
           HL - start address
Registers: AF, BC"
  0x0056)

(def LDIRMV
  "Function : Block transfer to memory from VRAM
Input    : BC - blocklength
           DE - Start address of memory
           HL - Start address of VRAM
Registers: All"
  0x0059)

(def LDIRVM
  "Function : Block transfer to VRAM from memory
Input    : BC - blocklength
           DE - Start address of VRAM
           HL - Start address of memory
Registers: All"
  0x005C)

(def CHGMOD
  "Function : Switches to given screenmode
Input    : A  - screen mode
Registers: All"
  0x005F)

(def CHGCLR
  "Function : Changes the screencolors
Input    : Foregroundcolor in FORCLR
           Backgroundcolor in BAKCLR
           Bordercolor in BDRCLR
Registers: All"
  0x0062)

(def NMI
  "Function : Executes (non-maskable interupt) handling routine"
  0x0066)

(def CLRSPR
  "Function : Initialises all sprites
Input    : SCRMOD
Registers: Alles"
  0x0069)

(def INITXT
  "Function : Schakelt naar SCREEN 0 (tekst-scherm met 40*24 tekens)
Input    : TXTNAM, TXTCGP
Registers: All"
  0x006C)

(def INIT32
  "Function : Switches to SCREEN 1 (text screen with 32*24 characters)
Input    : T32NAM, T32CGP, T32COL, T32ATR, T32PAT
Registers: All"
  0x006F)

(def INIGRP
  "Function : Switches to SCREEN 2 (high resolution screen with 256*192 pixels)
Input    : GRPNAM, GRPCGP, GRPCOL, GRPATR, GRPPAT
Registers: All"
  0x0072)

(def INIMLT
  "Function : Switches to SCREEN 3 (multi-color screen 64*48 pixels)
Input    : MLTNAM, MLTCGP, MLTCOL, MLTATR, MLTPAT
Registers: All"
  0x0075)

(def SETTXT
  "Function : Switches to VDP in SCREEN 0 mode
Input    : See INITXT
Registers: All"
  0x0078)

(def SETT32
  "Function : Schakelt VDP in SCREEN 1 modus
Input    : See INIT32
Registers: All"
  0x007B)

(def SETGRP
  "Function : Switches VDP to SCREEN 2 mode
Input    : See INIGRP
Registers: All"
  0x007E)

(def SETMLT
  "Function : Switches VDP to SCREEN 3 mode
Input    : See INIMLT
Registers: All"
  0x0081)

(def CALPAT
  "Function : Returns the address of the sprite pattern table
Input    : A  - Sprite ID
Output   : HL - For the address
Registers: AF, DE, HL"
  0x0084)

(def CALATR
  "Function : Returns the address of the sprite attribute table
Input    : A  - Sprite number
Output   : HL - For the address
Registers: AF, DE, HL"
  0x0087)

(def GSPSIZ
  "Function : Returns current sprite size
Output   : A  - Sprite-size in bytes
           C-flag set when size is 16*16 sprites otherwise C-flag is reset
Registers: AF"
  0x008A)

(def GRPPRT
  "Function : Displays a character on the graphic screen
Input    : A  - ASCII value of the character to print"
  0x008D)

(def GICINI
  "Function : Initialises PSG and sets initial value for the PLAY statement
Registers: All"
  0x0090)

(def WRTPSG
  "Function : Writes data to PSG-register
Input    : A  - PSG register number
           E  - data write"
  0x0093)

(def RDPSG
  "Function : Reads value from PSG-register
Input    : A  - PSG-register read
Output   : A  - value read"
  0x0096)

(def STRTMS
  "Function : Tests whether the PLAY statement is being executed as a background
           task. If not, begins to execute the PLAY statement
Registers: All"
  0x0099)

(def CHSNS
  "Function : Tests the status of the keyboard buffer
Output   : Z-flag set if buffer is empty, otherwise not set
Registers: AF"
  0x009C)

(def CHGET
  "Function : One character input (waiting)
Output   : A  - ASCII-code of the input character
Registers: AF"
  0x009F)

(def CHPUT
  "Function : Displays one character
Input    : A  - ASCII-code of character to display"
  0x00A2)

(def LPTOUT
  "Function : Sends one character to printer
Input    : A  - ASCII-code of character to send
Output   : C-flag set if failed
Registers: F"
  0x00A5)

(def LPTSTT
  "Function : Tests printer status
Output   : A  - #FF and Z-flag reset if printer is ready
                #00 and Z-flag set if not ready
Registers: AF"
  0x00A8)

(def CNVCHR
  "Function : tests for the graphic header and transforms the code
Input    : A  - charactercode
Output   : the C-flag is reset to not the graphic reader
           the C-flag and Z-flag are set to the transformed code is set in A
           the C-flag is set and Z-flag is reset to the untransformed code is set in A
Registers: AF"
  0x00AB)

(def PINLIN
  "Function : Stores in the specified buffer the character codes input until the return
           key or STOP key is pressed
Output   : HL - for the starting address of the buffer -1
           C-flag set when it ends with the STOP key
Registers: All"
  0x00AE)

(def INLIN
  "Function : Same as PINLIN except that AUGFLG (#F6AA) is set
Output   : HL - for the starting address of the buffer -1
           C-flag set when it ends with the STOP key
Registers: All"
  0x00B1)

(def QINLIN
  "Function : Prints a questionmark andone space
Output   : HL - for the starting address of the buffer -1
           C-flag set when it ends with the STOP key
Registers: All"
  0x00B4)

(def BREAKX
  "Function : Tests status of CTRL-STOP
Output   : C-flag set when pressed
Registers: AF
Remark   : In this routine, interrupts are inhibited"
  0x00B7)

(def ISCNTC
  "Function : Tests status of SHIFT-STOP"
  0x00BA)

(def CKCNTC
  "Function : Same as ISCNTC. used in Basic"
  0x00BD)

(def BEEP
  "Function : generates beep
Registers: All"
  0x00C0)

(def CLS
  "Function : Clears the screen
Registers: AF, BC, DE
Remark   : Z-flag must be set to be able to run this routine
           XOR A will do fine most of the time"
  0x00C3)

(def POSIT
  "Function : Plaatst cursor op aangegeven positie
Input    : H  - Y coordinate of cursor
           L  - X coordinate of cursor
Registers: AF"
  0x00C6)

(def FNKSB
  "Function : Tests whether the function key display is active (FNKFLG)
           If so, displays them, otherwise erase them
Input    : FNKFLG (#FBCE)
Registers: All"
  0x00C9)

(def ERAFNK
  "Function : Erase functionkey display
Registers: All"
  0x00CC)

(def DSPFNK
  "Function : Displays the function keys
Registers: All"
  0x00CF)

(def TOTEXT
  "Function : Forces the screen to be in the text mode
Registers: All"
  0x00D2)

(def GTSTCK
  "Function : Returns the joystick status
Input    : A  - Joystick number to test (0 = cursors, 1 = port 1, 2 = port 2)
Output   : A  - Direction
Registers: All"
  0x00D5)

(def GTTRIG
  "Function : Returns current trigger status
Input    : A  - trigger button to test
           0 = spacebar
           1 = port 1, button A
           2 = port 2, button A
           3 = port 1, button B
           4 = port 2, button B
Output   : A  - #00 trigger button not pressed
                #FF trigger button pressed
Registers: AF"
  0x00D8)

(def GTPAD
  "Function : Returns current touch pad status
Input    : A  - Function call number. Fetch device data first, then read.

           [ 0]   Fetch touch pad data from port 1 (#FF if available)
           [ 1]   Read X-position
           [ 2]   Read Y-position
           [ 3]   Read touchpad status from port 1 (#FF if pressed)

           [ 4]   Fetch touch pad data from port 2 (#FF if available)
           [ 5]   Read X-position
           [ 6]   Read Y-position
           [ 7]   Read touchpad status from port 2 (#FF if pressed)

Output   : A  - Value
Registers: All
Remark   : On MSX2, function call numbers 8-23 are forwarded to
           NEWPAD in the SubROM."
  0x00DB)

(def GTPDL
  "Function : Returns currenct value of paddle
Input    : A  - Paddle number
Output   : A  - Value
Registers: All"
  0x00DE)

(def TAPION
  "Function : Reads the header block after turning the cassette motor on
Output   : C-flag set if failed
Registers: All"
  0x00E1)

(def TAPIN
  "Function : Read data from the tape
Output   : A  - read value
           C-flag set if failed
Registers: All"
  0x00E4)

(def TAPIOF
  "Function : Stops reading from the tape"
  0x00E7)

(def TAPOON
  "Function : Turns on the cassette motor and writes the header
Input    : A  - #00 short header
            not #00 long header
Output   : C-flag set if failed
Registers: All"
  0x00EA)

(def TAPOUT
  "Function : Writes data on the tape
Input    : A  - data to write
Output   : C-flag set if failed
Registers: All"
  0x00ED)

(def TAPOOF
  "Function : Stops writing on the tape"
  0x00F0)

(def STMOTR
  "Function : Sets the cassette motor action
Input    : A  - #00 stop motor
                #01 start motor
                #FF reverse the current action
Registers: AF"
  0x00F3)

(def LFTQ
  "Function : Gives number of bytes in queue
Output   : A  - length of queue in bytes
Remark   : Internal use"
  0x00F6)

(def PUTQ
  "Function : Put byte in queue
Remark   : Internal use"
  0x00F9)

(def RIGHTC
  "Function : Shifts screenpixel to the right
Registers: AF"
  0x00FC)

(def LEFTC
  "Function : Shifts screenpixel to the left
Registers: AF"
  0x00FF)

(def UPC
  "Function : Shifts screenpixel up
Registers: AF"
  0x0102)

(def TUPC
  "Function : Tests whether UPC is possible, if possible, execute UPC
Output   : C-flag set if operation would end outside the screen
Registers: AF"
  0x0105)

(def DOWNC
  "Function : Shifts screenpixel down
Registers: AF"
  0x0108)

(def TDOWNC
  "Function : Tests whether DOWNC is possible, if possible, execute DOWNC
Output   : C-flag set if operation would end outside the screen
Registers: AF"
  0x010B)

(def SCALXY
  "Function : Scales X and Y coordinates"
  0x010E)

(def MAPXY
  "Function : Places cursor at current cursor address"
  0x0111)

(def FETCHC
  "Function : Gets current cursor addresses mask pattern
Output   : HL - Cursor address
           A  - Mask pattern"
  0x0114)

(def STOREC
  "Function : Record current cursor addresses mask pattern
Input    : HL - Cursor address
           A  - Mask pattern"
  0x0117)

(def SETATR
  "Function : Set attribute byte"
  0x011A)

(def READC
  "Function : Reads attribute byte of current screenpixel"
  0x011D)

(def SETC
  "Function : Returns currenct screenpixel of specificed attribute byte"
  0x0120)

(def NSETCX
  "Function : Set horizontal screenpixels"
  0x0123)

(def GTASPC
  "Function : Gets screen relations
Output   : DE, HL
Registers: DE, HL"
  0x0126)

(def PNTINI
  "Function : Initalises the PAINT instruction"
  0x0129)

(def SCANR
  "Function : Scans screenpixels to the right"
  0x012C)

(def SCANL
  "Function : Scans screenpixels to the left"
  0x012F)

(def CHGCAP
  "Function : Alternates the CAP lamp status
Input    : A  - #00 is lamp on
            not #00 is lamp off
Registers: AF"
  0x0132)

(def CHGSND
  "Function : Alternates the 1-bit sound port status
Input    : A  - #00 to turn off
            not #00 to turn on
Registers: AF"
  0x0135)

(def RSLREG
  "Function : Reads the primary slot register
Output   : A  - for the value which was read
           33221100
           ||||||- Pagina 0 (#0000-#3FFF)
           ||||--- Pagina 1 (#4000-#7FFF)
           ||----- Pagina 2 (#8000-#BFFF)
           ------- Pagina 3 (#C000-#FFFF)
Registers: A"
  0x0138)

(def WSLREG
  "Function : Writes value to the primary slot register
Input    : A  - value value to (see RSLREG)"
  0x013B)

(def RDVDP
  "Function : Reads VDP status register
Output   : A  - Value which was read
Registers: A"
  0x013E)

(def SNSMAT
  "Function : Returns the value of the specified line from the keyboard matrix
Input    : A  - for the specified line
Output   : A  - for data (the bit corresponding to the pressed key will be 0)
Registers: AF"
  0x0141)

(def PHYDIO
"Function : Executes I/O for mass-storage media like diskettes
Input    : F  - Set carry to write, reset carry to read
           A  - Drive number (0 = A:, 1 = B:, etc.)
           B  - Number of sectors
           C  - Media ID of the disk
           DE - Begin sector
           HL - Begin address in memory
Output   : F  - Carry set on error
           A  - Error code (only if carry set)
                0 = Write protected
                2 = Not ready
                4 = Data error
                6 = Seek error
                8 = Record not found
                10 = Write error
                12 = Bad parameter
                14 = Out of memory
                16 = Other error
           B  - Number of sectors actually written or read
Registers: All
Remark   : Interrupts may be disabled afterwards. On some hard disk interfaces,
           when bit 7 of register C is set, a 23-bit addressing scheme is used
           and bits 0-6 of register C contain bits 23-16 of the sector number."
  0x0144)

(def FORMAT
  "Function : Initialises mass-storage media like formatting of diskettes
Registers: All
Remark   : In minimum configuration only a HOOK is available"
  0x0147)

(def ISFLIO
  "Function : Tests if I/O to device is taking place
Output   : A  - #00 if not taking place
            not #00 if taking place
Registers: AF"
  0x014A)

(def OUTDLP
  "Function : Printer output
Input    : A  - code to print
Registers: F
Remark   : Differences with LPTOUT:
           1. TAB is expanded to spaces
           2. For non-MSX printers, Hiragana is transformed to katakana
              and graphic characters are transformed to 1-byte characters
           3. If failed, device I/O error occurs"
  0x014D)

(def GETVCP
  "Function : Returns pointer to play queue
Input    : A  - Channel number
Output   : HL - Pointer
Registers: AF
Remark   : Only used to play music in background"
  0x0150)

(def GETVC2
  "Function : Returns pointer to variable in queue number VOICEN (byte op #FB38)
Input    : L  - Pointer in play buffer
Output   : HL - Pointer
Registers: AF"
  0x0153)

(def KILBUF
  "Function : Clear keyboard buffer
Registers: HL"
  0x0156)

(def CALBAS
  "Function : Executes inter-slot call to the routine in BASIC interpreter
Input    : IX - for the calling address
Output   : Depends on the called routine
Registers: Depends on the called routine"
  0x0159)

(def SUBROM
  "Function : Calls a routine in SUB-ROM
Input    : IX - Address of routine in SUB-ROM
Output   : Depends on the routine
Registers: Alternative registers, IY
Remark   : Use of EXTROM or CALSLT is more convenient.
           In IX a extra value to the routine can be given by first
           PUSH'ing it to the stack."
  0x015C)

(def EXTROM
  "Function : Calls a routine in SUB-ROM. Most common way
Input    : IX - Address of routine in SUB-ROM
Output   : Depends on the routine
Registers: Alternative registers, IY
Remark   : Use: LD IX,address
                CALL EXTROM"
  0x015F)

(def CHKSLZ
  "Function : Search slots for SUB-ROM
Registers: Alles"
  0x0162)

(def CHKNEW
  "Function : Tests screen mode
Output   : C-flag set if screenmode = 5, 6, 7 or 8
Registers: AF"
  0x0165)

(def EOL
  "Function : Deletes to the end of the line
Input    : H  - x-coordinate of cursor
           L  - y-coordinate of cursor
Registers: All"
  0x0168)

(def BIGFIL
  "Function : Same function as FILVRM (total VRAM can be reached).
Input    : HL - address
           BC - length
           A  - data
Registers: AF,BC"
  0x016B)

(def NSETRD
  "Function : Same function as SETRD.(with full 16 bits VRAM-address)
Input    : HL - VRAM address
Registers: AF"
  0x016E)

(def NSTWRT
  "Function : Same function as SETWRT.(with full 16 bits VRAM-address)
Input    : HL - VRAM address
Registers: AF"
  0x0171)

(def NRDVRM
  "Function : Reads VRAM like in RDVRM.(with full 16 bits VRAM-address)
Input    : HL - VRAM address
Output   : A  - Read value
Registers: F"
  0x0174)

(def NWRVRM
  "Function : Writes to VRAM like in WRTVRM.(with full 16 bits VRAM-address)
Input    : HL - VRAM address
           A  - Value to write
Registers: AF"
  0x0177)

(def RDBTST
  "Function : Read value of I/O poort #F4
Input    : none
Output   : A = value read
Registers: AF"
  0x017A)

(def WRBTST
  "Function : Write value to I/O poort #F4
Input    : A = value to write
           Bit 7 shows the MSX 2+ startup screen when reset, otherwise it's skipped.
Output   : none
Registers: none"
  0x017D)

(def CHGCPU
  "Function : Changes CPU mode
Input    : A = LED 0 0 0 0 0 x x
                |            0 0 = Z80 (ROM) mode
                |            0 1 = R800 ROM  mode
                |            1 0 = R800 DRAM mode
               LED indicates whether the Turbo LED is switched with the CPU
Output   : none
Registers: none"
  0x0180)

(def GETCPU
  "Function : Returns current CPU mode
Input    : none
Output   : A = 0 0 0 0 0 0 x x
                           0 0 = Z80 (ROM) mode
                           0 1 = R800 ROM  mode
                           1 0 = R800 DRAM mode
Registers: AF"
  0x0183)

(def PCMPLY
  "Function : Plays specified memory area through the PCM chip
Input    : A = v 0 0 0 0 0 x x
               |           | |
               |           +-+-- Quality parameter (Speed: 0 = Fast)
               +---------------- VRAM usage flag
           HL= Start address in RAM or VRAM
           BC= Length of area to play
           D = Bit 0 = Bit 17 of area length when using VRAM
           E = Bit 0 = Bit 17 os start address when using VRAM
Output   : C-flag set when aborted with CTRL-STOP
Registers: all"
  0x0186)

(def PCMREC
  "Function : Records audio using the PCM chip into the specified memory area
Input    : A = v t t t t c x x
               | | | | | | | |
               | | | | | | +-+-- Quality parameter (Speed: 0 = Fast)
               | | | | | +------ Zero-data compression
               | +-+-+-+-------- Treshold
               +---------------- VRAM usage flag
           HL= Start address in RAM or VRAM
           BC= Length of area to play
           D = Bit 0 = Bit 17 of area length when using VRAM
           E = Bit 0 = Bit 17 os start address when using VRAM
Output   : C-flag set when aborted with CTRL-STOP
Registers: all"
  0x0189)
