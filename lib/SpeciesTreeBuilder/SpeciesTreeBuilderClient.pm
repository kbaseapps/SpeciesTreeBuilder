package SpeciesTreeBuilder::SpeciesTreeBuilderClient;

use JSON::RPC::Client;
use POSIX;
use strict;
use Data::Dumper;
use URI;
use Bio::KBase::Exceptions;
my $get_time = sub { time, 0 };
eval {
    require Time::HiRes;
    $get_time = sub { Time::HiRes::gettimeofday() };
};

use Bio::KBase::AuthToken;

# Client version should match Impl version
# This is a Semantic Version number,
# http://semver.org
our $VERSION = "0.1.0";

=head1 NAME

SpeciesTreeBuilder::SpeciesTreeBuilderClient

=head1 DESCRIPTION


A KBase module: SpeciesTreeBuilder


=cut

sub new
{
    my($class, $url, @args) = @_;
    

    my $self = {
	client => SpeciesTreeBuilder::SpeciesTreeBuilderClient::RpcClient->new,
	url => $url,
	headers => [],
    };

    chomp($self->{hostname} = `hostname`);
    $self->{hostname} ||= 'unknown-host';

    #
    # Set up for propagating KBRPC_TAG and KBRPC_METADATA environment variables through
    # to invoked services. If these values are not set, we create a new tag
    # and a metadata field with basic information about the invoking script.
    #
    if ($ENV{KBRPC_TAG})
    {
	$self->{kbrpc_tag} = $ENV{KBRPC_TAG};
    }
    else
    {
	my ($t, $us) = &$get_time();
	$us = sprintf("%06d", $us);
	my $ts = strftime("%Y-%m-%dT%H:%M:%S.${us}Z", gmtime $t);
	$self->{kbrpc_tag} = "C:$0:$self->{hostname}:$$:$ts";
    }
    push(@{$self->{headers}}, 'Kbrpc-Tag', $self->{kbrpc_tag});

    if ($ENV{KBRPC_METADATA})
    {
	$self->{kbrpc_metadata} = $ENV{KBRPC_METADATA};
	push(@{$self->{headers}}, 'Kbrpc-Metadata', $self->{kbrpc_metadata});
    }

    if ($ENV{KBRPC_ERROR_DEST})
    {
	$self->{kbrpc_error_dest} = $ENV{KBRPC_ERROR_DEST};
	push(@{$self->{headers}}, 'Kbrpc-Errordest', $self->{kbrpc_error_dest});
    }

    #
    # This module requires authentication.
    #
    # We create an auth token, passing through the arguments that we were (hopefully) given.

    {
	my %arg_hash2 = @args;
	if (exists $arg_hash2{"token"}) {
	    $self->{token} = $arg_hash2{"token"};
	} elsif (exists $arg_hash2{"user_id"}) {
	    my $token = Bio::KBase::AuthToken->new(@args);
	    if (!$token->error_message) {
	        $self->{token} = $token->token;
	    }
	}
	
	if (exists $self->{token})
	{
	    $self->{client}->{token} = $self->{token};
	}
    }

    my $ua = $self->{client}->ua;	 
    my $timeout = $ENV{CDMI_TIMEOUT} || (30 * 60);	 
    $ua->timeout($timeout);
    bless $self, $class;
    #    $self->_validate_version();
    return $self;
}




=head2 construct_species_tree

  $out = $obj->construct_species_tree($input)

=over 4

=item Parameter and return types

=begin html

<pre>
$input is a SpeciesTreeBuilder.ConstructSpeciesTreeParams
$out is a SpeciesTreeBuilder.ConstructSpeciesTreeOutput
ConstructSpeciesTreeParams is a reference to a hash where the following keys are defined:
	new_genomes has a value which is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
	genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
	out_workspace has a value which is a string
	out_tree_id has a value which is a string
	out_genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
	copy_genomes has a value which is an int
	use_ribosomal_s9_only has a value which is an int
	nearest_genome_count has a value which is an int
ws_genome_id is a string
ws_genomeset_id is a string
ConstructSpeciesTreeOutput is a reference to a hash where the following keys are defined:
	output_result_id has a value which is a SpeciesTreeBuilder.ws_tree_id
	report_name has a value which is a string
	report_ref has a value which is a string
ws_tree_id is a string

</pre>

=end html

=begin text

$input is a SpeciesTreeBuilder.ConstructSpeciesTreeParams
$out is a SpeciesTreeBuilder.ConstructSpeciesTreeOutput
ConstructSpeciesTreeParams is a reference to a hash where the following keys are defined:
	new_genomes has a value which is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
	genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
	out_workspace has a value which is a string
	out_tree_id has a value which is a string
	out_genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
	copy_genomes has a value which is an int
	use_ribosomal_s9_only has a value which is an int
	nearest_genome_count has a value which is an int
ws_genome_id is a string
ws_genomeset_id is a string
ConstructSpeciesTreeOutput is a reference to a hash where the following keys are defined:
	output_result_id has a value which is a SpeciesTreeBuilder.ws_tree_id
	report_name has a value which is a string
	report_ref has a value which is a string
ws_tree_id is a string


=end text

=item Description

Build a species tree out of a set of given genome references.

=back

=cut

 sub construct_species_tree
{
    my($self, @args) = @_;

# Authentication: required

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function construct_species_tree (received $n, expecting 1)");
    }
    {
	my($input) = @args;

	my @_bad_arguments;
        (ref($input) eq 'HASH') or push(@_bad_arguments, "Invalid type for argument 1 \"input\" (value was \"$input\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to construct_species_tree:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'construct_species_tree');
	}
    }

    my $url = $self->{url};
    my $result = $self->{client}->call($url, $self->{headers}, {
	    method => "SpeciesTreeBuilder.construct_species_tree",
	    params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'construct_species_tree',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method construct_species_tree",
					    status_line => $self->{client}->status_line,
					    method_name => 'construct_species_tree',
				       );
    }
}
 


=head2 find_close_genomes

  $return = $obj->find_close_genomes($params)

=over 4

=item Parameter and return types

=begin html

<pre>
$params is a SpeciesTreeBuilder.FindCloseGenomesParams
$return is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
FindCloseGenomesParams is a reference to a hash where the following keys are defined:
	query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
	max_mismatch_percent has a value which is an int
ws_genome_id is a string

</pre>

=end html

=begin text

$params is a SpeciesTreeBuilder.FindCloseGenomesParams
$return is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
FindCloseGenomesParams is a reference to a hash where the following keys are defined:
	query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
	max_mismatch_percent has a value which is an int
ws_genome_id is a string


=end text

=item Description

Find closely related public genomes based on COG of ribosomal s9 subunits.

=back

=cut

 sub find_close_genomes
{
    my($self, @args) = @_;

# Authentication: required

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function find_close_genomes (received $n, expecting 1)");
    }
    {
	my($params) = @args;

	my @_bad_arguments;
        (ref($params) eq 'HASH') or push(@_bad_arguments, "Invalid type for argument 1 \"params\" (value was \"$params\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to find_close_genomes:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'find_close_genomes');
	}
    }

    my $url = $self->{url};
    my $result = $self->{client}->call($url, $self->{headers}, {
	    method => "SpeciesTreeBuilder.find_close_genomes",
	    params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'find_close_genomes',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method find_close_genomes",
					    status_line => $self->{client}->status_line,
					    method_name => 'find_close_genomes',
				       );
    }
}
 


=head2 guess_taxonomy_path

  $return = $obj->guess_taxonomy_path($params)

=over 4

=item Parameter and return types

=begin html

<pre>
$params is a SpeciesTreeBuilder.GuessTaxonomyPathParams
$return is a string
GuessTaxonomyPathParams is a reference to a hash where the following keys are defined:
	query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
ws_genome_id is a string

</pre>

=end html

=begin text

$params is a SpeciesTreeBuilder.GuessTaxonomyPathParams
$return is a string
GuessTaxonomyPathParams is a reference to a hash where the following keys are defined:
	query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
ws_genome_id is a string


=end text

=item Description

Search for taxonomy path from closely related public genomes (approach similar to find_close_genomes).

=back

=cut

 sub guess_taxonomy_path
{
    my($self, @args) = @_;

# Authentication: required

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function guess_taxonomy_path (received $n, expecting 1)");
    }
    {
	my($params) = @args;

	my @_bad_arguments;
        (ref($params) eq 'HASH') or push(@_bad_arguments, "Invalid type for argument 1 \"params\" (value was \"$params\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to guess_taxonomy_path:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'guess_taxonomy_path');
	}
    }

    my $url = $self->{url};
    my $result = $self->{client}->call($url, $self->{headers}, {
	    method => "SpeciesTreeBuilder.guess_taxonomy_path",
	    params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'guess_taxonomy_path',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method guess_taxonomy_path",
					    status_line => $self->{client}->status_line,
					    method_name => 'guess_taxonomy_path',
				       );
    }
}
 


=head2 build_genome_set_from_tree

  $genomeset_ref = $obj->build_genome_set_from_tree($params)

=over 4

=item Parameter and return types

=begin html

<pre>
$params is a SpeciesTreeBuilder.BuildGenomeSetFromTreeParams
$genomeset_ref is a SpeciesTreeBuilder.ws_genomeset_id
BuildGenomeSetFromTreeParams is a reference to a hash where the following keys are defined:
	tree_ref has a value which is a SpeciesTreeBuilder.ws_tree_id
	genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
ws_tree_id is a string
ws_genomeset_id is a string

</pre>

=end html

=begin text

$params is a SpeciesTreeBuilder.BuildGenomeSetFromTreeParams
$genomeset_ref is a SpeciesTreeBuilder.ws_genomeset_id
BuildGenomeSetFromTreeParams is a reference to a hash where the following keys are defined:
	tree_ref has a value which is a SpeciesTreeBuilder.ws_tree_id
	genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
ws_tree_id is a string
ws_genomeset_id is a string


=end text

=item Description



=back

=cut

 sub build_genome_set_from_tree
{
    my($self, @args) = @_;

# Authentication: required

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function build_genome_set_from_tree (received $n, expecting 1)");
    }
    {
	my($params) = @args;

	my @_bad_arguments;
        (ref($params) eq 'HASH') or push(@_bad_arguments, "Invalid type for argument 1 \"params\" (value was \"$params\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to build_genome_set_from_tree:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'build_genome_set_from_tree');
	}
    }

    my $url = $self->{url};
    my $result = $self->{client}->call($url, $self->{headers}, {
	    method => "SpeciesTreeBuilder.build_genome_set_from_tree",
	    params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'build_genome_set_from_tree',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method build_genome_set_from_tree",
					    status_line => $self->{client}->status_line,
					    method_name => 'build_genome_set_from_tree',
				       );
    }
}
 
  
sub status
{
    my($self, @args) = @_;
    if ((my $n = @args) != 0) {
        Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
                                   "Invalid argument count for function status (received $n, expecting 0)");
    }
    my $url = $self->{url};
    my $result = $self->{client}->call($url, $self->{headers}, {
        method => "SpeciesTreeBuilder.status",
        params => \@args,
    });
    if ($result) {
        if ($result->is_error) {
            Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
                           code => $result->content->{error}->{code},
                           method_name => 'status',
                           data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
                          );
        } else {
            return wantarray ? @{$result->result} : $result->result->[0];
        }
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method status",
                        status_line => $self->{client}->status_line,
                        method_name => 'status',
                       );
    }
}
   

sub version {
    my ($self) = @_;
    my $result = $self->{client}->call($self->{url}, $self->{headers}, {
        method => "SpeciesTreeBuilder.version",
        params => [],
    });
    if ($result) {
        if ($result->is_error) {
            Bio::KBase::Exceptions::JSONRPC->throw(
                error => $result->error_message,
                code => $result->content->{code},
                method_name => 'build_genome_set_from_tree',
            );
        } else {
            return wantarray ? @{$result->result} : $result->result->[0];
        }
    } else {
        Bio::KBase::Exceptions::HTTP->throw(
            error => "Error invoking method build_genome_set_from_tree",
            status_line => $self->{client}->status_line,
            method_name => 'build_genome_set_from_tree',
        );
    }
}

sub _validate_version {
    my ($self) = @_;
    my $svr_version = $self->version();
    my $client_version = $VERSION;
    my ($cMajor, $cMinor) = split(/\./, $client_version);
    my ($sMajor, $sMinor) = split(/\./, $svr_version);
    if ($sMajor != $cMajor) {
        Bio::KBase::Exceptions::ClientServerIncompatible->throw(
            error => "Major version numbers differ.",
            server_version => $svr_version,
            client_version => $client_version
        );
    }
    if ($sMinor < $cMinor) {
        Bio::KBase::Exceptions::ClientServerIncompatible->throw(
            error => "Client minor version greater than Server minor version.",
            server_version => $svr_version,
            client_version => $client_version
        );
    }
    if ($sMinor > $cMinor) {
        warn "New client version available for SpeciesTreeBuilder::SpeciesTreeBuilderClient\n";
    }
    if ($sMajor == 0) {
        warn "SpeciesTreeBuilder::SpeciesTreeBuilderClient version is $svr_version. API subject to change.\n";
    }
}

=head1 TYPES



=head2 ws_tree_id

=over 4



=item Description

A workspace ID that references a Tree data object.
@id ws KBaseTrees.Tree


=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 ws_genome_id

=over 4



=item Description

A workspace ID that references a Genome data object.
@id ws KBaseGenomes.Genome KBaseGenomeAnnotations.GenomeAnnotation


=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 ws_genomeset_id

=over 4



=item Description

A workspace ID that references a GenomeSet data object.
@id ws KBaseSearch.GenomeSet


=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 ConstructSpeciesTreeParams

=over 4



=item Description

Input data type for construct_species_tree method. Method produces object of Tree type.

new_genomes - (optional) the list of genome references to use in constructing a tree; either
    new_genomes or genomeset_ref field should be defined.
genomeset_ref - (optional) reference to genomeset object; either new_genomes or genomeset_ref
    field should be defined.
out_workspace - (required) the workspace to deposit the completed tree
out_tree_id - (optional) the name of the newly constructed tree (will be random if not present or null)
out_genomeset_ref - the name of the output genomeset object
copy_genomes - 1 means copy genomes to user workspace; 0 means refer only to the public genomes
use_ribosomal_s9_only - (optional) 1 means only one protein family (Ribosomal S9) is used for 
    tree construction rather than all 49 improtant families, default value is 0.
nearest_genome_count - (optional) defines maximum number of public genomes nearest to
    requested genomes that will show in output tree.


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
new_genomes has a value which is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
out_workspace has a value which is a string
out_tree_id has a value which is a string
out_genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
copy_genomes has a value which is an int
use_ribosomal_s9_only has a value which is an int
nearest_genome_count has a value which is an int

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
new_genomes has a value which is a reference to a list where each element is a SpeciesTreeBuilder.ws_genome_id
genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
out_workspace has a value which is a string
out_tree_id has a value which is a string
out_genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id
copy_genomes has a value which is an int
use_ribosomal_s9_only has a value which is an int
nearest_genome_count has a value which is an int


=end text

=back



=head2 ConstructSpeciesTreeOutput

=over 4



=item Description

Output is a report, and a Tree object


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
output_result_id has a value which is a SpeciesTreeBuilder.ws_tree_id
report_name has a value which is a string
report_ref has a value which is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
output_result_id has a value which is a SpeciesTreeBuilder.ws_tree_id
report_name has a value which is a string
report_ref has a value which is a string


=end text

=back



=head2 FindCloseGenomesParams

=over 4



=item Description

Input data type for find_close_genomes method. Method produces list of refereces to public genomes similar to query.

query_genome - (required) query genome reference
max_mismatch_percent - (optional) defines maximum mismatch percentage when compare aminoacids from user genome with 
    public genomes (defualt value is 5).


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
max_mismatch_percent has a value which is an int

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id
max_mismatch_percent has a value which is an int


=end text

=back



=head2 GuessTaxonomyPathParams

=over 4



=item Description

Input data type for guess_taxonomy_path method. Method produces taxonomy path string.

query_genome - (required) query genome reference


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
query_genome has a value which is a SpeciesTreeBuilder.ws_genome_id


=end text

=back



=head2 BuildGenomeSetFromTreeParams

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
tree_ref has a value which is a SpeciesTreeBuilder.ws_tree_id
genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
tree_ref has a value which is a SpeciesTreeBuilder.ws_tree_id
genomeset_ref has a value which is a SpeciesTreeBuilder.ws_genomeset_id


=end text

=back



=cut

package SpeciesTreeBuilder::SpeciesTreeBuilderClient::RpcClient;
use base 'JSON::RPC::Client';
use POSIX;
use strict;

#
# Override JSON::RPC::Client::call because it doesn't handle error returns properly.
#

sub call {
    my ($self, $uri, $headers, $obj) = @_;
    my $result;


    {
	if ($uri =~ /\?/) {
	    $result = $self->_get($uri);
	}
	else {
	    Carp::croak "not hashref." unless (ref $obj eq 'HASH');
	    $result = $self->_post($uri, $headers, $obj);
	}

    }

    my $service = $obj->{method} =~ /^system\./ if ( $obj );

    $self->status_line($result->status_line);

    if ($result->is_success) {

        return unless($result->content); # notification?

        if ($service) {
            return JSON::RPC::ServiceObject->new($result, $self->json);
        }

        return JSON::RPC::ReturnObject->new($result, $self->json);
    }
    elsif ($result->content_type eq 'application/json')
    {
        return JSON::RPC::ReturnObject->new($result, $self->json);
    }
    else {
        return;
    }
}


sub _post {
    my ($self, $uri, $headers, $obj) = @_;
    my $json = $self->json;

    $obj->{version} ||= $self->{version} || '1.1';

    if ($obj->{version} eq '1.0') {
        delete $obj->{version};
        if (exists $obj->{id}) {
            $self->id($obj->{id}) if ($obj->{id}); # if undef, it is notification.
        }
        else {
            $obj->{id} = $self->id || ($self->id('JSON::RPC::Client'));
        }
    }
    else {
        # $obj->{id} = $self->id if (defined $self->id);
	# Assign a random number to the id if one hasn't been set
	$obj->{id} = (defined $self->id) ? $self->id : substr(rand(),2);
    }

    my $content = $json->encode($obj);

    $self->ua->post(
        $uri,
        Content_Type   => $self->{content_type},
        Content        => $content,
        Accept         => 'application/json',
	@$headers,
	($self->{token} ? (Authorization => $self->{token}) : ()),
    );
}



1;
