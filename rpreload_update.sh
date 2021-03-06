#!/bin/bash
#
# rpreload - Resource pack management made easy.
# Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification,
# are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
# list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright notice,
# this list of conditions and the following disclaimer in the documentation and/or
# other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
# ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
# ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#


# ------ Script settings. ------

# Desired name of rpreload.jar.
DESIRED_NAME=rpreload.jar
# Desired version of rpreload that will be downloaded.
DESIRED_VERSION=1.0

# ------ Do not edit below this line! ------
if [ -f $DESIRED_NAME ];
then
echo " Backing up old rpreload jar ($DESIRED_NAME)..."
mv $DESIRED_NAME $DESIRED_NAME.bak
fi
echo " Downloading new rpreload jar from maven repo..."
wget -4 -q http://repo.matejkormuth.eu/eu/matejkormuth/rpreload/$DESIRED_VERSION/rpreload-$DESIRED_VERSION-jar-with-dependencies.jar
echo " Renaming downloaded artifact to desired name..."
mv rpreload-$DESIRED_VERSION-jar-with-dependencies.jar $DESIRED_NAME
chmod +x $DESIRED_NAME